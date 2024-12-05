package Entity;

public class Method {
    private Integer id;
    private String type;
    private String description;


    @Override
    public String toString() {
        return "Method{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Method() {
    }

    public Method(Integer id, String type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
