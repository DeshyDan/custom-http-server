package connection;


import model.HttpHeader;
import model.HttpRequest;
import model.HttpResponse;
import parser.HttpParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler implements Runnable {

    private Socket socket;


    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            HttpRequest httpRequest = HttpParser.parse(in);
            System.out.println(httpRequest);
            byte[] response = response(httpRequest);
            out.write(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] response(HttpRequest req) {
        HttpResponse response;
        if (req.getPath().startsWith("/echo")) {
            String body = req.getPath().replace("/echo/", "");
            response = new HttpResponse("HTTP/1.1 200 OK", body);
        }
        else if (req.getPath().startsWith("/user-agent")){
            HttpHeader userAgentHeader = req.getHeader("User-Agent");
            String body = userAgentHeader.getValues().getFirst();
            response = new HttpResponse("HTTP/1.1 200 OK", body);
        }
        else if (req.getPath().equals("/")) {
            response = new HttpResponse("HTTP/1.1 200 OK", "");
        } else {
            response = new HttpResponse("HTTP/1.1 404 Not Found", "Something went wrong");
        }
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}
