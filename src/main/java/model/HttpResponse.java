package model;

public class HttpResponse {
    private String statusLine;
    private String body;
    private String contentType;
    private int contentLength;


    public HttpResponse(String statusLine, String body) {
        this.statusLine = statusLine;
        this.body = body;
        setContentLength(body);
        setContentType();
    }

    public int getContentLength() {
        return contentLength;
    }

    private void setContentType() {
        contentType = "text/plain";
    }

    private void setContentLength(String body) {

        contentLength = body.length();

    }

    @Override
    public String toString() {
        String lineBreak = "\r\n\r\n";
        return statusLine + lineBreak +
                "Content-Type: " + contentType + lineBreak +
                "Content-Length: " + contentLength + lineBreak +
                body + lineBreak;

    }
}
