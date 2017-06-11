package david.bicilock;

public class Photo {

    public Long position;
    public String id;
    public String serialNumber;
    public String url;

    public Photo(Long position, String id, String serialNumber, String url) {
        this.position = position;
        this.id = id;
        this.serialNumber = serialNumber;
        this.url = url;
    }

    public Photo(){}

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}