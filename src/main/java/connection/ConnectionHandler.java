package connection;


import model.*;
import parser.HttpParser;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class ConnectionHandler implements Runnable {

    private Socket socket;
    private String[] args;
    private InputStream in;
    private OutputStream out;


    public ConnectionHandler(Socket socket, String[] args) {
        this.socket = socket;
        this.args = args;
    }

    @Override
    public void run() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
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

                CompressedHttpResponse result = getHandler(req);
                byte[] byteResponse = result.getResponse().toString().getBytes(StandardCharsets.UTF_8);
                byte[] gzip = result.getByteArrayOutputStream().toByteArray();
                ByteBuffer buffer = ByteBuffer.allocate(byteResponse.length + gzip.length);
                buffer.put(byteResponse);
                buffer.put(gzip);
                return buffer.array();


            case "POST":
                response = postHandler(req);
                break;

            default:
                response = new HttpResponse.Builder().statusLine(HttpStatus.NOT_FOUND.toString()).body("").build();

        }

        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

    private HttpResponse postHandler(HttpRequest req) throws IOException {
        Path directory = Paths.get(args[1]);
        String fileName = req.getPath().substring(7);


        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        Path filepath = directory.resolve(fileName);
        try (FileWriter file = new FileWriter(filepath.toString())) {
            file.write(req.getBody());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new HttpResponse.Builder().statusLine(HttpStatus.CREATED.toString()).contentType("application/octet-stream").body(req.getBody()).build();

    }

    private CompressedHttpResponse getHandler(HttpRequest req) throws IOException {
        HttpResponse response;
        ByteArrayOutputStream bodyStream;
        if (req.getPath().startsWith("/echo")) {
            String body = req.getPath().replace("/echo/", "");
            bodyStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


            gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.close();

            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body)
                    .contentLength(bodyStream.toByteArray().length)
                    .contentType("text/plain")
                    .encodingType(req.getHeader("Accept-Encoding"))
                    .build();
        } else if (req.getPath().startsWith("/user-agent")) {
            HttpHeader userAgentHeader = req.getHeader("User-Agent");
            String body = userAgentHeader.getValues().getLast();
            bodyStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


            gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.close();

            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body(body).contentType("text/plain")
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

                bodyStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


                gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
                gzipOutputStream.close();

                response = new HttpResponse.Builder()
                        .statusLine(HttpStatus.OK.toString())
                        .body(body)
                        .contentType("application/octet-stream")
                        .build();

            } else {
                String body = "File not found";
                bodyStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


                gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
                gzipOutputStream.close();

                response = new HttpResponse.Builder()
                        .statusLine(HttpStatus.NOT_FOUND.toString())
                        .body(body)
                        .contentType("text/plain")
                        .build();
            }


        } else if (req.getPath().equals("/")) {
            bodyStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


            gzipOutputStream.close();

            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.OK.toString())
                    .body("")
                    .contentType("text/plain")
                    .build();
        } else {
            String body = "something went wrong";
            bodyStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bodyStream);


            gzipOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.close();

            response = new HttpResponse.Builder()
                    .statusLine(HttpStatus.NOT_FOUND.toString())
                    .body("Something went wrong")
                    .contentType("text/plain")
                    .build();

        }

        return new CompressedHttpResponse(response, bodyStream);


    }
}
