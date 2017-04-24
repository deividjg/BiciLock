package david.bicilock;

/**
 * Created by david on 23/04/2017.
 */

public class User {

    String email, password, name, town, province, phone;

    public User(String email, String password, String name, String town, String province, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.town = town;
        this.province = province;
        this.phone = phone;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

