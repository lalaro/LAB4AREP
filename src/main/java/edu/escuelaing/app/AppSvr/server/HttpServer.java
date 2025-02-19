package edu.escuelaing.app.AppSvr.server;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.HashMap;
import edu.escuelaing.app.AppSvr.server.DefaultResponse;
import edu.escuelaing.app.AppSvr.EciBoot;

public class HttpServer {
    private static Map<String, BiFunction<Request, String, String>> servicios = new ConcurrentHashMap<>();
    private static String staticFilePath = "/app/static";
    private static volatile boolean running = true;
    private static ExecutorService threadPool;
    private static ServerSocket serverSocket;
    private static final int SHUTDOWN_TIMEOUT = 60;
    private static final int MAX_THREADS = 50;
    private static final int QUEUE_CAPACITY = 100;

    public static void main(String[] args) throws IOException, URISyntaxException {
        threadPool = new ThreadPoolExecutor(
                10,
                MAX_THREADS,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        serverSocket = new ServerSocket(35000);
        System.out.println("Servidor HTTP corriendo en el puerto 35000");
        EciBoot.loadComponents();
        setupShutdownHook();
        startConsoleListener();

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClientConnection(clientSocket));
            } catch (SocketException e) {
                if (running) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
            }
        }
    }

    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Iniciando apagado por señal del sistema...");
            shutdown();
        }));
    }

    private static void startConsoleListener() {
        new Thread(() -> {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (running) {
                    String input = consoleReader.readLine();
                    if (input != null && input.equalsIgnoreCase("shutdown")) {
                        System.out.println("Iniciando apagado por comando...");
                        shutdown();
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en el lector de consola: " + e.getMessage());
            }
        }).start();
    }

    private static void shutdown() {
        running = false;
        System.out.println("Iniciando secuencia de apagado...");

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando el servidor: " + e.getMessage());
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS)) {
                    System.out.println("Forzando terminación de tareas pendientes...");
                    threadPool.shutdownNow();
                    if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("Algunas tareas no pudieron ser terminadas!");
                    }
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Servidor apagado exitosamente.");
    }

    private static void handleClientConnection(Socket clientSocket) {
        try (
                clientSocket;
                OutputStream out = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            boolean isFirstLine = true;
            String file = "";
            String queryString = "";

            while ((inputLine = in.readLine()) != null && running) {
                if (isFirstLine) {
                    String[] parts = inputLine.split(" ");
                    file = parts[1];
                    if (file.contains("?")) {
                        String[] splitPath = file.split("\\?");
                        file = splitPath[0];
                        queryString = splitPath[1];
                    }
                    isFirstLine = false;
                }
                if (!in.ready()) break;
            }

            if (!running) return;

            Request request = new Request(queryString);
            System.out.println("Solicitud recibida: " + file + " | Query: " + queryString);

            if (isStaticFile(file)) {
                serveStaticFiles(file, out);
            } else {
                String response;
                if (servicios.containsKey(file)) {
                    response = processRequest(file, request);
                } else if (EciBoot.services.containsKey(file)) {
                    Map<String, String> queryParams = parseQueryString(queryString);
                    response = processEciBootRequest(file, queryParams);
                } else {
                    response = "HTTP/1.1 404 Not Found\r\n\r\n{\"error\": \"Recurso no encontrado\"}";
                }
                out.write(response.getBytes());
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error manejando la conexión del cliente: " + e.getMessage());
            }
        }
    }

    private static String processEciBootRequest(String path, Map<String, String> queryParams) {
        try {
            if (!EciBoot.services.containsKey(path)) {
                return "HTTP/1.1 404 Not Found\r\n\r\n{\"error\": \"Ruta no encontrada\"}";
            }

            String result = EciBoot.executeService(path, queryParams);
            if (result == null) {
                return "HTTP/1.1 500 Internal Server Error\r\n\r\n{\"error\": \"Error procesando la solicitud\"}";
            }

            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"result\": \"" + result + "\"}";
        } catch (Exception e) {
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return queryParams;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParams;
    }

    private static boolean isStaticFile(String file) {
        return file.endsWith(".html") || file.endsWith(".css") || file.endsWith(".js") ||
                file.endsWith(".png") || file.endsWith(".jpg") || file.endsWith(".jpeg");
    }

    public static void get(String route, BiFunction<Request, String, String> f) {
        servicios.put(route, f);
    }

    public static void staticfiles(String path) {
        staticFilePath = path;
    }

    private static void serveStaticFiles(String filePath, OutputStream out) throws IOException {
        String basePath = "/app/static";
        File requestedFile = new File(basePath + filePath);
        String contentType = determineContentType(filePath);

        if (requestedFile.exists() && requestedFile.isFile()) {
            String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";
            out.write(header.getBytes());

            try (FileInputStream fileInputStream = new FileInputStream(requestedFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            DefaultResponse.generateFormResponse(out);
        }
    }

    private static String determineContentType(String file) {
        if (file.endsWith(".png")) {
            return "png";
        } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
            return "jpeg";
        } else if (file.endsWith(".html")) {
            return "html";
        } else if (file.endsWith(".css")) {
            return "css";
        } else if (file.endsWith(".js")) {
            return "javascript";
        } else {
            return "octet-stream";
        }
    }

    private static String processRequest(String path, Request request) {
        BiFunction<Request, String, String> servicio = servicios.get(path);

        if (servicio != null) {
            String responseBody = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"result\": \"" + servicio.apply(request, "") + "\"}";
            return responseBody;
        } else {
            return "HTTP/1.1 404 Not Found\r\n\r\n{\"error\": \"Service not found\"}";
        }
    }
}