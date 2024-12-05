package Entity;

import java.util.List;

public class Request {
    private Integer id;
    private String name;
    private String address;
    private String body;
    private Method method;
    private Header header;
    private List<QueryString> queryStrings;

    public Request() {
    }

    public Request(String name, String address, String body, Method method, Header header, List<QueryString> queryStrings) {
        this.name = name;
        this.address = address;
        this.body = body;
        this.method = method;
        this.header = header;
        this.queryStrings = queryStrings;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", body='" + body + '\'' +
                ", method=" + method +
                ", header=" + header +
                ", queryStrings=" + queryStrings +
                '}';
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<QueryString> getQueryStrings() {
        return queryStrings;
    }

    public void setQueryStrings(List<QueryString> queryStrings) {
        this.queryStrings = queryStrings;
    }
}
