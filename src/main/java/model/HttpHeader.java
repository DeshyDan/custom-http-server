package model;

import java.util.List;

public class HttpHeader {
    private String key;
    private List<String> values;

    public HttpHeader(){

    }
    public HttpHeader(String key, List<String> values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "key='" + key + '\'' +
                ", values=" + values +
                '}';
    }
}
