package parser;

import model.HttpHeader;
import model.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class HttpParser {

    public static HttpRequest parse(InputStream in) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String input;
        List<String> headerLines = new ArrayList<>();
        String startLine;
        try {
            startLine = bufferedReader.readLine();

            while (!(input = bufferedReader.readLine()).equalsIgnoreCase("")) {
                headerLines.add(input);
            }

            HttpRequest request = parseStartLine(startLine);

            List<HttpHeader> headers = parseHeaders(headerLines);

            request.setHeaders(headers);
            return request;
        } catch (IOException | HttpParseException e) {
            throw new RuntimeException(e);
        }


    }

    public static HttpRequest parseStartLine(String startLine) throws HttpParseException {
        HttpRequest req = new HttpRequest();
        try {
            List<String> parts = List.of(startLine.split(" "));

            req.setHttpMethod(parts.get(0));
            req.setPath(parts.get(1));
            req.setVersion(parts.get(2));

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unable to parse the start line");
            throw new HttpParseException("Unable to parse the start line");
        }

        return req;
    }

    public static List<HttpHeader> parseHeaders(List<String> headerLines) {
        List<HttpHeader> headers = new ArrayList<>();
        for (String headerLine : headerLines) {
            try {
                String key = headerLine.split(":")[0];
                String value = headerLine.split(":")[1];

                List<String> valueList = List.of(value.split(" "));

                HttpHeader header = new HttpHeader(key, valueList);
                headers.add(header);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return headers;
    }


    public static class HttpParseException extends Exception {
        public HttpParseException(String message) {
            super(message);
        }
    }


}
