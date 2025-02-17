package edu.escuelaing.app.AppSvr.server;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> parameters;

    public Request(String queryString) {
        parameters = new HashMap<>();
        if (queryString != null) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public String getValues(String key) {
        return parameters.get(key);
    }
}
