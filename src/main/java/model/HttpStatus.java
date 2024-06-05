package model;

public enum HttpStatus {
    OK(200, "OK"),
CREATED(201, "Created"),
    NOT_FOUND(404, "Not Found");


    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;

    }

    @Override
    public String toString() {
        String httpVersion = "HTTP/1.1";
        return httpVersion + " " + code + " " + reasonPhrase;
    }


}
