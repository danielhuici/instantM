package info.androidhive.loginandregistration.contact;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.androidhive.loginandregistration.chats.Chat;
/**
 * Define las propiedades de un contacto.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class Contact extends Chat implements Serializable  {
    private String name;
    private Date lastConnection;
    private int userId;

    public Contact(String name) {
        this.name = name;
        super.isGroup = false;
    }
    public Contact(String name, int userId) {
        this.name = name;
        this.userId = userId;
        super.isGroup = false;
    }
    public static List<Contact> JSONToContacts(JSONArray contactsListJSON) throws JSONException {
        List<Contact> vContacts = new ArrayList<>();
        for (int i = 0; i< contactsListJSON.length(); i++) {
            JSONObject groups = contactsListJSON.getJSONObject(i);
            JSONObject data = groups.getJSONObject("data");
            Contact contact = new Contact(data.getString("name"), data.getInt("id_user"));
            vContacts.add(contact);
        }
        return vContacts;
    }

    public int getUserId() {
        return userId;
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



    @Override
    public String getTitle() {
        return name;
    }

    @Override
    protected String getSubtitle() {
        return "Ultima conexion";
    }

    @Override
    protected Bitmap getPic() {
        return pic;
    }

    public boolean nameLike(String name) {
        return this.name.toLowerCase().startsWith(name.toLowerCase()) || name.equals("");
    }
}
