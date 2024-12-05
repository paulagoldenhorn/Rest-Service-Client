package Entity;

public class Header {
    private Integer id;
    private String header_key;
    private String header_value;

    @Override
    public String toString() {
        return "Header{" +
                "id=" + id +
                ", header_key='" + header_key + '\'' +
                ", header_value='" + header_value + '\'' +
                '}';
    }

    public Header() {
    }

    public Header(String header_key, String header_value) {
        this.header_key = header_key;
        this.header_value = header_value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeader_key() {
        return header_key;
    }

    public void setHeader_key(String header_key) {
        this.header_key = header_key;
    }

    public String getHeader_value() {
        return header_value;
    }

    public void setHeader_value(String header_value) {
        this.header_value = header_value;
    }
}
