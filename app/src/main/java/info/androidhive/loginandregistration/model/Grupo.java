package info.androidhive.loginandregistration.model;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Grupo {
    private String name;
    private Date lastConnection;
    private Bitmap foto;

    public Grupo(String name, String lastConnectionText) throws ParseException {
        this.name = name;
        this.lastConnection = new SimpleDateFormat("dd-MM-yyyy").parse(lastConnectionText);
        this.foto = foto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
