package connection;


import model.HttpHeader;
import model.HttpRequest;
import model.HttpResponse;
import model.HttpStatus;
import parser.HttpParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        switch (req.getHttpMethod()) {
            case "GET":

                response = getHandler(req);


                break;

            case "POST":
                response = postHandler(req);
                break;

            default:
                response = new HttpResponse.Builder()
                        .statusLine(HttpStatus.NOT_FOUND.toString())
                        .body("")
                        .build();

        }

        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

    private HttpResponse postHandler(HttpRequest req) throws IOException {
        Path directory = Paths.get(args[1]);
        String fileName = req.getPath().substring(7);


        Path directoryPath = Files.createDirectory(directory);

        Path filepath = directoryPath.resolve(fileName);
        try (FileWriter file = new FileWriter(filepath.toString())) {
            file.write(req.getBody());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new HttpResponse.Builder()
                .statusLine(HttpStatus.OK.toString())
                .contentType("application/octet-stream")
                .body(req.getBody())
                .build();

    }

    private HttpResponse getHandler(HttpRequest req) throws IOException {
        if (req.getPath().startsWith("/echo")) {
            String body = req.getPath().replace("/echo/", "");
            return new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body)
                    .contentType("text/plain")
                    .build();
        } else if (req.getPath().startsWith("/user-agent")) {
            HttpHeader userAgentHeader = req.getHeader("User-Agent");
            String body = userAgentHeader.getValues().getLast();
            return new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body)
                    .contentType("text/plain")
                    .build();
        } else if (req.getPath().startsWith("/files")) {


            String filepath = args[1];
            String fileName = req.getPath().substring(7);
            //            TODO: Implement error handling
            File file = new File(filepath, fileName);

            if (file.exists()) {

                byte[] fileContent = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(fileContent);
                fileInputStream.close();
                String body = new String(fileContent, StandardCharsets.UTF_8);


                return new HttpResponse.Builder()
                        .statusLine(HttpStatus.CREATED.toString())
                        .body(body)
                        .contentType("application/octet-stream")
                        .build();

            } else {
                return new HttpResponse.Builder()
                        .statusLine(HttpStatus.NOT_FOUND.toString())
                        .body("File not found ")
                        .contentType("text/plain")
                        .build();
            }


        } else if (req.getPath().equals("/")) {
            return new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body("")
                    .contentType("text/plain")
                    .build();
        } else {
            return new HttpResponse.Builder()
                    .statusLine(HttpStatus.NOT_FOUND.toString())
                    .body("Something went wrong")
                    .contentType("text/plain")
                    .build();

        }


    }
}
