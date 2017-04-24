package david.bicilock;

/**
 * Created by david on 23/04/2017.
 */

public class Usuario {

    String email, password, nombre, poblacion, provincia, telefono;

    public Usuario(String email, String password, String nombre, String poblacion, String provincia, String telefono) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.provincia = provincia;

        this.telefono = telefono;
    }

    public Usuario(){

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

