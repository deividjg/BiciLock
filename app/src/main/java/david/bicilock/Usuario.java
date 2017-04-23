package david.bicilock;

/**
 * Created by david on 23/04/2017.
 */

public class Usuario {

    String email, telefono;
    int nombre, localidad, provincia, password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public int getLocalidad() {
        return localidad;
    }

    public void setLocalidad(int localidad) {
        this.localidad = localidad;
    }

    public int getProvincia() {
        return provincia;
    }

    public void setProvincia(int provincia) {
        this.provincia = provincia;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public Usuario(String email, String telefono, int nombre, int localidad, int provincia, int password) {
        this.email = email;
        this.telefono = telefono;
        this.nombre = nombre;

        this.localidad = localidad;
        this.provincia = provincia;
        this.password = password;
    }

    public Usuario(){

    }
}

