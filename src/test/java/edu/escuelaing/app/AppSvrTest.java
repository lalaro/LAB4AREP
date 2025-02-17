package edu.escuelaing.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import edu.escuelaing.app.AppSvr.EciBoot;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppSvrTest {
    private static Thread serverThread;

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

    @Test
    public void testGreetingWithParameter() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Lala");
        String response = EciBoot.executeService("/greeting", params);
        assertEquals("Hola Lala", response, "Debe saludar con el nombre proporcionado");
    }

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

    @Test
    public void testSquareEndpoint() {
        Map<String, String> params = new HashMap<>();
        params.put("n", "100");
        String response = EciBoot.executeService("/square", params);
        assertEquals("10000", response, "Debe retornar el cuadrado del número");
    }

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

    @Test
    public void testMissingRequiredParam() {
        Map<String, String> params = new HashMap<>(); // No se envían parámetros
        String response = EciBoot.executeService("/square", params);
        assertTrue(response.contains("Error"), "Debe manejar falta de parámetros requeridos");
    }

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
}