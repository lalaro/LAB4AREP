package edu.escuelaing.app.AppSvr.controllers;

import edu.escuelaing.app.AppSvr.RestController;
import edu.escuelaing.app.AppSvr.GetMapping;
import edu.escuelaing.app.AppSvr.RequestParam;

@RestController
public class MathController {
    @GetMapping("/square")
    public static String square(@RequestParam("n") String n) {
        try {
            int num = Integer.parseInt(n);
            return String.valueOf(num * num);
        } catch (NumberFormatException e) {
            return "Invalid input: 'n' must be an integer";
        }
    }
}