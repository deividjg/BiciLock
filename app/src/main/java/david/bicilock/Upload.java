package david.bicilock;

public class Upload {

    public String name;
    public Long id;

    public Upload(Long id, String name, String url) {
        this.id = id;
        this.name = name;
    }

    public Upload(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}