package edu.escuelaing.app.AppSvr;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.reflections.Reflections;

public class EciBoot {
    public static Map<String, Method> services = new HashMap<>();
    private static final Logger logger = Logger.getLogger(EciBoot.class.getName());

    public static void loadComponents() {
        try {
            Reflections reflections = new Reflections("edu.escuelaing.app.AppSvr.controllers");
            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

            for (Class<?> controllerClass : controllers) {
                logger.info("Loading component: " + controllerClass.getName());

                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping annotation = method.getAnnotation(GetMapping.class);
                        services.put(annotation.value(), method);
                        logger.info("Registered method: " + method.getName() + " at path " + annotation.value());
                    }
                }
            }

            logger.info("Components loaded successfully.");
        } catch (Exception ex) {
            logger.severe("Unexpected error in loadComponents(): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String simulateRequest(String route) {
        try {
            if (!services.containsKey(route)) {
                return "HTTP/1.1 404 Not Found\r\n\r\n{\"error\": \"Ruta no encontrada\"}";
            }
            Method method = services.get(route);
            if (method == null) {
                return "HTTP/1.1 500 Internal Server Error\r\n\r\n{\"error\": \"Método no encontrado\"}";
            }

            String result = executeService(route, new HashMap<>());
            return "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"result\": \"" + result + "\"}";
        } catch (Exception e) {
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    public static String executeService(String path, Map<String, String> queryParams) {
        try {
            Method method = services.get(path);
            if (method == null) {
                return null;
            }

            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                if (param.isAnnotationPresent(RequestParam.class)) {
                    RequestParam annotation = param.getAnnotation(RequestParam.class);
                    String paramName = annotation.value().isEmpty() ? param.getName() : annotation.value();
                    String paramValue = queryParams.getOrDefault(paramName, annotation.defaultValue());

                    if (paramValue == null || paramValue.isEmpty()) {
                        paramValue = annotation.defaultValue();
                    }

                    if ((paramValue == null || paramValue.isEmpty()) && annotation.required()) {
                        throw new RuntimeException("Required parameter '" + paramName + "' is missing");
                    }

                    args[i] = paramValue;
                }
            }

            return (String) method.invoke(null, args);
        } catch (Exception e) {
            logger.severe("Error executing service: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    //Si se desea solo probar el ECIBoot
    /*
    public static void main(String[] args){
        System.out.println("Iniciando EciBoot...");

        if (args.length == 0) {
            System.out.println("No se proporcionaron componentes para cargar.");
            return;
        }

        loadComponents(args);

        System.out.println("Simulando petición a '/greeting'...");
        String response = simulateRequest("/greeting");
        System.out.println(response);
    }
    public static void loadComponents(String[] args) {
        try {
            System.out.println("Cargando componente: " + args[0]);

            Class<?> c = Class.forName(args[0]);

            if (!c.isAnnotationPresent(RestController.class)) {
                System.out.println("La clase " + args[0] + " no es un RestController. Saliendo...");
                return;
            }

            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    GetMapping a = m.getAnnotation(GetMapping.class);
                    services.put(a.value(), m);
                    System.out.println("Método registrado: " + m.getName() + " en ruta " + a.value());
                }
            }

            System.out.println("Componentes cargados correctamente.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: No se encontró la clase " + args[0]);
        } catch (Exception ex) {
            System.out.println("Error inesperado en loadComponents(): " + ex.getMessage());
        }
    }
    */
}