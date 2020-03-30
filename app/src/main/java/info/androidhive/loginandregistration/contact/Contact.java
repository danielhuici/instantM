package info.androidhive.loginandregistration.contact;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contact {
    private String name;
    private Date lastConnection;
    private Bitmap foto;

    public Contact(String name) {
        this.name = name;
        this.foto = foto;
    }

    public static List<Contact> JSONToContact(JSONArray contactsListJSON) throws JSONException {
        List<Contact> vContacts = new ArrayList<>();
        for (int i = 0; i< contactsListJSON.length(); i++) {
            JSONObject groups = contactsListJSON.getJSONObject(i);
            JSONObject data = groups.getJSONObject("data");
            Contact contact = new Contact(data.getString("name"));
            vContacts.add(contact);
        }
        return vContacts;
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
        return "";
        //return dateFormat.format(lastConnection);
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

    public boolean nameLike(String name) {
        return this.name.toLowerCase().startsWith(name.toLowerCase()) || name.equals("");
    }
}
