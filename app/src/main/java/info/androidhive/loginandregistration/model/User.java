package info.androidhive.loginandregistration.model;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private String username;
    private String email;
    private Date lastConnection;
    private Bitmap foto;


    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.foto = foto;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastConnection() {
        return lastConnection;
    }
    public String getLastConnectionText() {
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
        return dateFormat.format(lastConnection);
    }

    public void setLastConnection(Date lastConnection) {
        this.lastConnection = lastConnection;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getEmail() {
        return email;
    }

    public boolean nameLike(String name) {
        return this.username.toLowerCase().startsWith(name.toLowerCase()) || name.equals("");
    }
}
