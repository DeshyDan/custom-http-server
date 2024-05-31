package connection;


import model.HttpHeader;
import model.HttpRequest;
import model.HttpResponse;
import model.HttpStatus;
import parser.HttpParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler implements Runnable {

    private Socket socket;
    private String[] args;


    public ConnectionHandler(Socket socket, String[] args) {
        this.socket = socket;
        this.args = args;
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

    public byte[] response(HttpRequest req) throws IOException {
        HttpResponse response;
        if (req.getPath().startsWith("/echo")) {
            String body = req.getPath().replace("/echo/", "");
            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body)
                    .contentType("text/plain")
                    .build();
        } else if (req.getPath().startsWith("/user-agent")) {
            HttpHeader userAgentHeader = req.getHeader("User-Agent");
            String body = userAgentHeader.getValues().getLast();
            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body)
                    .contentType("text/plain")
                    .build();
        } else if (req.getPath().startsWith("/files")) {


//            TODO: Implement error handling
            String filepath = args[1];
            String fileName = req.getPath().substring(7);
            File file = new File(filepath, fileName);

            if (file.exists()) {

                byte[] fileContent = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(fileContent);
                fileInputStream.close();
                String body = new String(fileContent, StandardCharsets.UTF_8);


                response = new HttpResponse.Builder()
                        .statusLine(HttpStatus.OK.toString())
                        .body(body)
                        .contentType("application/octet-stream")
                        .build();

            } else {
                response = new HttpResponse.Builder()
                        .statusLine(HttpStatus.NOT_FOUND.toString())
                        .body("File not found ")
                        .contentType("text/plain")
                        .build();

                System.out.println("Something went wrong");
            }


        } else if (req.getPath().equals("/")) {
            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body("")
                    .contentType("text/plain")
                    .build();
        } else {
            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body("Something went wrong")
                    .contentType("text/plain")
                    .build();

        }
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}
