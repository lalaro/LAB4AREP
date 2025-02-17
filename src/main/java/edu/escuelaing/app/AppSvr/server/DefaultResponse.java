package edu.escuelaing.app.AppSvr.server;

import java.io.OutputStream;
import java.io.IOException;

public class DefaultResponse {

    public static void generateFormResponse(OutputStream out) throws IOException {
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <title>Form Example</title>"
                + "    <meta charset=\"UTF-8\">"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "</head>"
                + "<body>"
                + "    <h1>Form with GET</h1>"
                + "    <form action=\"/hello\">"
                + "        <label for=\"name\">Name:</label><br>"
                + "        <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>"
                + "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">"
                + "    </form> "
                + "    <div id=\"getrespmsg\"></div>"
                + ""
                + "    <script>"
                + "        function loadGetMsg() {"
                + "            let nameVar = document.getElementById(\"name\").value;"
                + "            const xhttp = new XMLHttpRequest();"
                + "            xhttp.onload = function() {"
                + "                document.getElementById(\"getrespmsg\").innerHTML ="
                + "                this.responseText;"
                + "            }"
                + "            xhttp.open(\"GET\", \"/hello?name=\"+nameVar);"
                + "            xhttp.send();"
                + "        }"
                + "    </script>"
                + ""
                + "    <h1>Form with POST</h1>"
                + "    <form action=\"/hellopost\">"
                + "        <label for=\"postname\">Name:</label><br>"
                + "        <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>"
                + "        <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">"
                + "    </form>"
                + ""
                + "    <div id=\"postrespmsg\"></div>"
                + ""
                + "    <script>"
                + "        function loadPostMsg(name){"
                + "            let url = \"/hellopost?name=\" + name.value;"
                + ""
                + "            fetch (url, {method: 'POST'})"
                + "                .then(x => x.text())"
                + "                .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);"
                + "        }"
                + "    </script>"
                + "</body>"
                + "</html>";

        out.write(outputLine.getBytes());
    }
}
