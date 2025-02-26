package edu.escuelaing.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import edu.escuelaing.app.AppSvr.EciBoot;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import edu.escuelaing.app.AppSvr.server.Request;
import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;
public class AppSvrTest {
    private static Thread serverThread;
    private static int port;
    private static final String DefaultResponse = "Respuesta por defecto"; // Definir antes de usar


    @BeforeAll
    public static void setUp() {
        serverThread = new Thread(() -> {
            EciBoot.loadComponents();
        });
        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    /*
    @Test
    public void testGreetingWithParameter() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Lala");
        String response = EciBoot.executeService("/greeting", params);
        assertEquals("Hola Lala", response, "Debe saludar con el nombre proporcionado");
    }*/

    @Test
    public void testPiEndpoint() {
        String response = EciBoot.simulateRequest("/pi");
        assertTrue(response.contains("200 OK"), "La respuesta debe contener 200 OK");
        assertTrue(response.contains("3.141592653589793"), "Debe retornar el valor de PI");
    }

    @Test
    public void testNotFoundRoute() {
        String response = EciBoot.simulateRequest("/notfound");
        assertTrue(response.contains("404 Not Found"), "Debe retornar error 404 para rutas inexistentes");
    }

    @Test
    public void testGreetingEndpoint() {
        String response = EciBoot.simulateRequest("/greeting");
        assertTrue(response.contains("200 OK"), "La respuesta debe contener 200 OK");
        assertTrue(response.contains("Hola World"), "La respuesta debe contener el saludo por defecto");
    }

    /*
    @Test
    public void testSquareEndpoint() {
        Map<String, String> params = new HashMap<>();
        params.put("n", "100");
        String response = EciBoot.executeService("/square", params);
        assertEquals("10000", response, "Debe retornar el cuadrado del número");
    }*/

    @Test
    public void testSquareInvalidInput() {
        Map<String, String> params = new HashMap<>();
        params.put("n", "abc");
        String response = EciBoot.executeService("/square", params);
        assertEquals("Invalid input: 'n' must be an integer", response, "Debe manejar entradas inválidas correctamente");
    }

    @Test
    public void testSimulatedRequest() {
        String response = EciBoot.simulateRequest("/pi");
        assertTrue(response.contains("200 OK"), "Debe contener 200 OK");
        assertTrue(response.contains("3.141592653589793"), "Debe devolver el valor de PI");
    }

    /*
    @Test
    public void testMissingRequiredParam() {
        Map<String, String> params = new HashMap<>(); // No se envían parámetros
        String response = EciBoot.executeService("/square", params);
        assertTrue(response.contains("Error"), "Debe manejar falta de parámetros requeridos");
    }*/

    @Test
    public void testExecuteServiceWithNumber() {
        Map<String, String> params = new HashMap<>();
        params.put("n", "5");
        String response = EciBoot.executeService("/square", params);
        assertEquals("25", response, "Debe calcular correctamente el cuadrado de 5");
    }

    @Test
    public void testRegisteredServicesExist() {
        Set<String> registeredServices = EciBoot.services.keySet();
        assertTrue(registeredServices.contains("/greeting"), "El servicio '/greeting' debe estar registrado.");
        assertTrue(registeredServices.contains("/square"), "El servicio '/square' debe estar registrado.");
    }

    @Test
    public void testEmptyQueryString() {
        String queryString = "";

        Request request = new Request(queryString);

        assertNull(request.getValues("name"));
        assertNull(request.getValues("age"));
        assertNull(request.getValues("city"));
    }

    @Test
    public void testDuplicateParameters() {
        String queryString = "name=John&name=Alice&age=25";
        Request request = new Request(queryString);
        assertEquals("Alice", request.getValues("name"));
        assertEquals("25", request.getValues("age"));
    }

    @Test
    public void testSingleParameter() {
        String queryString = "name=John";
        Request request = new Request(queryString);
        assertEquals("John", request.getValues("name"));
    }

    @Test
    public void testSpecialCharactersInParameters() {
        String queryString = "name=John Doe&message=Hello%20World";
        Request request = new Request(queryString);
        assertEquals("John Doe", request.getValues("name"));
        assertEquals("Hello%20World", request.getValues("message"));
    }


    @Test
    public void testMultipleParameters() {
        Request request = new Request("name=John&age=25&city=Bogota");
        assertEquals("John", request.getValues("name"), "El valor de 'name' debe ser 'John'");
        assertEquals("25", request.getValues("age"), "El valor de 'age' debe ser '25'");
        assertEquals("Bogota", request.getValues("city"), "El valor de 'city' debe ser 'Bogota'");
    }


    @Test
    public void testNullQueryString() {
        Request request = new Request(null);
        assertNull(request.getValues("name"), "El valor de 'name' debe ser null");
    }
}