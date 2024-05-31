package model;

public class HttpResponse {
    private final String statusLine;
    private final String body;
    private String contentType;
    private int contentLength;


    public HttpResponse(String statusLine, String body) {
        this.statusLine = statusLine;
        this.body = body;
        setContentLength(body);

    }

    private HttpResponse(Builder builder) {
        this.statusLine = builder.statusLine;
        this.body = builder.body;
        this.contentType = builder.contentType;
        setContentLength(body);

    }


    public int getContentLength() {
        return contentLength;
    }

    private void setContentLength(String body) {

        contentLength = body.length();

    }

    @Override
    public String toString() {
        String sectionBreak = "\r\n\r\n";
        String lineBreak = "\r\n";
        return statusLine + lineBreak + "Content-Type: " + contentType + lineBreak + "Content-Length: " + contentLength + sectionBreak + body + lineBreak;

    }
//    TODO: Make this it's own class
    public static class Builder {
        private String statusLine;
        private String body;
        private String contentType;

        public Builder statusLine(String statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }


        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
