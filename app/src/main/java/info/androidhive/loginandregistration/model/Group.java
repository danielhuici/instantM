package info.androidhive.loginandregistration.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group {
    private String name;
    private Date lastConnection;
    private String description;
    private Bitmap foto;

    public Group(String name, String description)  {
        this.name = name;
        this.description = description;
        this.foto = foto;
    }

    public static List<Group> JSONToGroups(JSONArray groupsListJSON) throws JSONException {
        List<Group> vGroups = new ArrayList<>();
        for (int i = 0; i< groupsListJSON.length(); i++) {
            JSONObject groups = groupsListJSON.getJSONObject(i);
            JSONObject data = groups.getJSONObject("data");
            Group group = new Group(data.getString("name"), data.getString("description"));
            vGroups.add(group);
        }
        return vGroups;
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
        return dateFormat.format(new Date());
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
