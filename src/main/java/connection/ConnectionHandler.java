package connection;


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
            response = new HttpResponse("HTTP/1.1 200 OK", "abc");
        } else if (req.getPath().equals("/")) {
            response = new HttpResponse("HTTP/1.1 200 OK", "abc");
        } else {
            response = new HttpResponse("HTTP/1.1 404 Not Found", "abc");
        }
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}
