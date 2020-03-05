package info.androidhive.loginandregistration.app;

import android.graphics.Bitmap;

public class Grupo {
 String nombre;
 String apellido;
    Bitmap foto;

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellidos) {
        this.apellido = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public Grupo(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
