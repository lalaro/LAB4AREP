package edu.escuelaing.app.AppSvr.controllers;

import edu.escuelaing.app.AppSvr.RestController;
import edu.escuelaing.app.AppSvr.GetMapping;
import edu.escuelaing.app.AppSvr.RequestParam;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/pi")
    public static String pi() {
        return Double.toString(Math.PI);
    }
}
