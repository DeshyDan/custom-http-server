package model;

public class HttpResponse {
    private final String statusLine;
    private final String body;
    private String contentType;
    private String encodingType;
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
        this.encodingType = builder.encodingType;
        if (builder.contentLength > 0 && this.encodingType != null) {
            setContentLength(builder.contentLength);
        } else {
            setContentLength(body);
        }


    }


    public int getContentLength() {
        return this.contentLength;
    }

    private void setContentLength(String body) {

        this.contentLength = body.length();

    }

    private void setContentLength(int length) {
        this.contentLength = length;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    @Override
    public String toString() {
        String sectionBreak = "\r\n\r\n";
        String lineBreak = "\r\n";
        if (encodingType != null) {
//            TODO: add missing body
            return statusLine + lineBreak + "Content-Encoding: " + encodingType + lineBreak + "Content-Type: " + contentType + lineBreak + "Content-Length: " + contentLength + sectionBreak;

        } else {
            return statusLine + lineBreak + "Content-Type: " + contentType + lineBreak + "Content-Length: " + contentLength + sectionBreak + body + lineBreak;

        }

    }

    //    TODO: Make this it's own class
    public static class Builder {
        private String statusLine;
        private String body;
        private String contentType;
        private String encodingType;
        private int contentLength;

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

        public Builder contentLength(int contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder encodingType(HttpHeader encodingType) {
            if (encodingType.getKey() != null) {
                for (String encoding : encodingType.getValues()) {
                    if (encoding.equals("gzip")) {
                        this.encodingType = encoding;
                    }
                }

            }


            return this;
        }


        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
