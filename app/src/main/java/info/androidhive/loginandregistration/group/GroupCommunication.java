package info.androidhive.loginandregistration.group;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.utils.Tupla;

public class GroupCommunication extends Observable {
    public static final String CREATE_GROUP_OK = "CREATE_GROUP_OK";
    public static final String EDIT_GROUP_OK = "EDIT_GROUP_OK";
    public static final String CREATE_GROUP_ERROR = "CREATE_GROUP_ERROR";
    public static final String EDIT_GROUP_ERROR = "EDIT_GROUP_ERROR";
    public static final String GET_USER_GROUPS_OK = "GET_USER_GROUPS_OK";
    public static final String GET_USER_GROUPS_ERROR = "GET_USER_GROUPS_ERROR";
    public static final String INSERT_MEMBERS_OK = "INSERT_MEMBERS_OK";
    public static final String INSERT_MEMBERS_ERROR = "INSERT_MEMBERS_ERROR";
    public static final String GET_MEMBERS_OK = "GET_MEMBERS_OK";
    public static final String GET_MEMBERS_ERROR = "GET_MEMBERS_ERROR";

    private static final String URL_GET_MEMBERS = "http://34.69.44.48/instantm/obtener_integrantes.php" ;
    public static String URL_CREATE_GROUP = "http://34.69.44.48/instantm/crear_grupo.php";
    public static String URL_GET_GROUPS = "http://34.69.44.48/instantm/obtener_grupos.php";
    public static String URL_INSERT_MEMBERS = "http://34.69.44.48/instantm/insertar_integrantes.php";



    public void crateGroup(final Group groupToCreate, final String username) {
        CreateGroupListener createGroupListener = new CreateGroupListener();
        System.out.println(groupToCreate.getDescription());
        System.out.println(groupToCreate.getName());
        //System.out.println(groupToCreate.getPicBLOB());
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_CREATE_GROUP, createGroupListener, createGroupListener) {


            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(groupToCreate.getId()));
                params.put("mode", mode);
                params.put("name", groupToCreate.getName());
                params.put("username", username);
                params.put("description", groupToCreate.getDescription());
                params.put("pic", groupToCreate.getPicBLOB());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getUserGroups(final String username) {
        GetUserGroupsListener getUserGroupsListener = new GetUserGroupsListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_GROUPS, getUserGroupsListener, getUserGroupsListener) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void insertMembers(final List<Contact> vMembers, final int id_grupo) {
        InsertMembersListener insertMembersListener = new InsertMembersListener();
        String members = "";
        for (Contact c : vMembers)
            members = members + c.getName() + ";";
        System.out.println(members);
        final String membersToInsert = members;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_INSERT_MEMBERS, insertMembersListener, insertMembersListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_group", String.valueOf(id_grupo));
                params.put("members", membersToInsert);


                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getMembers(final int group_id) {
        GetMembersListener getMembersListener = new GetMembersListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_MEMBERS, getMembersListener, getMembersListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("group_id", String.valueOf(group_id));
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, "");
    }


    class GetMembersListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray groupsListJSON = jObj.getJSONArray("members");
                    System.out.println(jObj.toString());
                    //TODO CAMBIAR ESTO A OBJETOS DEL TIPO CONTACTS
                    List<Contact> vMembers = new ArrayList<>();
                    for (int i = 0; i< groupsListJSON.length(); i++) {
                        vMembers.add(new Contact(groupsListJSON.getJSONObject(i).getJSONObject("data").getString("name")));
                    }
                    setChanged();
                    notifyObservers(new Tupla<>(GET_MEMBERS_OK, vMembers));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(GET_MEMBERS_ERROR,"ERROR RESPONSE"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(GET_MEMBERS_ERROR,"ERROR JSON"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_MEMBERS_ERROR,"ERROR RESPONSE"));
        }
    }

    class InsertMembersListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(INSERT_MEMBERS_OK,null));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(INSERT_MEMBERS_ERROR,"ERROR RESPONSE"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(INSERT_MEMBERS_ERROR,"ERROR JSON"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(INSERT_MEMBERS_ERROR,"ERROR RESPONSE"));
        }
    }

    class CreateGroupListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_GROUP_OK, jObj.getInt("id")));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR RESPONSE"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR JSON"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error.getStackTrace());
            setChanged();
            notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR SERVER"));
        }
    }

    class GetUserGroupsListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {

                    List<Group> groups = Group.JSONToGroups(jObj.getJSONArray("groups"));
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_GROUPS_OK, groups));

                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "JSON ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_USER_GROUPS_ERROR, "RESPONSE ERROR"));
        }
    }

    class EditGroupListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(EDIT_GROUP_OK,null));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(EDIT_GROUP_ERROR,"ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR"));
        }
    }
}
