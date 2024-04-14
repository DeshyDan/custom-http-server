package model;

import java.util.List;


public class HttpRequest {
    private String httpMethod;
    private String path;
    private String version;
    private List<HttpHeader> headers;

    public HttpRequest(){}

    public HttpRequest(String httpMethod, String path, String version, List<HttpHeader> headers) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.version = version;
        this.headers = headers;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HttpHeader> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                '}';
    }
}
