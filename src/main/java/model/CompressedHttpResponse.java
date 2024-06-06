package model;

import java.io.ByteArrayOutputStream;

public class CompressedHttpResponse {


    private HttpResponse response;
    private ByteArrayOutputStream byteArrayOutputStream;

    public CompressedHttpResponse(HttpResponse response, ByteArrayOutputStream byteArrayOutputStream) {
        this.response = response;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }
}
