package Entity;

public class QueryString {
    private Integer id;
    private String query_key;
    private String query_value;

    @Override
    public String toString() {
        return "QueryString{" +
                "id=" + id +
                ", query_key='" + query_key + '\'' +
                ", query_value='" + query_value + '\'' +
                '}';
    }
    public QueryString() {
    }
    public QueryString(String query_key, String query_value) {
        this.query_key = query_key;
        this.query_value = query_value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery_key() {
        return query_key;
    }

    public void setQuery_key(String query_key) {
        this.query_key = query_key;
    }

    public String getQuery_value() {
        return query_value;
    }

    public void setQuery_value(String query_value) {
        this.query_value = query_value;
    }
}
