package info.androidhive.loginandregistration.group;

import android.graphics.Bitmap;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.loginandregistration.chats.Chat;
/**
 * Define las propiedades de un grupo
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class Group extends Chat implements Serializable {
    private String description;
    private int id;
    private int administratorId;

    public Group()  {
        super.isGroup = true;
    }

    public static List<Group> JSONToGroups(JSONArray groupsListJSON) throws JSONException {
        List<Group> vGroups = new ArrayList<>();
        for (int i = 0; i< groupsListJSON.length(); i++) {
            vGroups.add(JSONToGroup(groupsListJSON.getJSONObject(i)));
        }
        return vGroups;
    }

    /**
     /**
     * Convierte un objeto json en un grupo.
     * @param groupsListJSON json con la información del grupo.
     * @return Un objeto de grupo.
     * @throws JSONException
     */
    private static Group JSONToGroup(JSONObject groupsListJSON) throws JSONException {
            JSONObject data = groupsListJSON.getJSONObject("data");
            Group g = new Group();
            g.setName(data.getString("name"));
            g.setDescription(data.getString("description"));
            g.setId(data.getInt("id_chat_group"));
            System.out.println(data.getString("id_user"));
            if(!data.getString("id_user").trim().equalsIgnoreCase("null"))
                g.setAdministratorId(Integer.parseInt(data.getString("id_user")));
            else
                g.setAdministratorId(-1);
        return g;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTitle() {
        return this.name;
    }

    @Override
    protected String getSubtitle() {
        return this.description;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }

    String getPicBLOB() {
        if(pic == null)
            return "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        return Base64.encodeToString(bArray, Base64.DEFAULT);
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

    private void setAdministratorId(int administratorId) {
        this.administratorId = administratorId;
    }

    @Override
    public String toString() {
        return this.name + " " + this.description+ " " + this.id + " " + this.getPicBLOB();
    }

    boolean isAdmin(int currentID) {
        return currentID == administratorId;
    }

    public boolean nameLike(String searchString) {
        return name.toLowerCase().startsWith(searchString.trim().toLowerCase()) || searchString.equalsIgnoreCase("");
    }
}
