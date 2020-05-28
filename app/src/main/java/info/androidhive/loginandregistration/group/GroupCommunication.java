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
import info.androidhive.loginandregistration.utils.AppController;
import info.androidhive.loginandregistration.utils.Tupla;
/**
 * Gestiona la comunicacion con el servidor en el ámbito de los grupos.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class GroupCommunication extends Observable {
    static final String CREATE_GROUP_OK = "CREATE_GROUP_OK";
    static final String CREATE_GROUP_ERROR = "CREATE_GROUP_ERROR";
    public static final String GET_USER_GROUPS_OK = "GET_USER_GROUPS_OK";
    public static final String GET_USER_GROUPS_ERROR = "GET_USER_GROUPS_ERROR";
    static final String INSERT_MEMBERS_OK = "INSERT_MEMBERS_OK";
    static final String INSERT_MEMBERS_ERROR = "INSERT_MEMBERS_ERROR";
    static final String GET_MEMBERS_OK = "GET_MEMBERS_OK";
    static final String GET_MEMBERS_ERROR = "GET_MEMBERS_ERROR";
    static final String LEAVE_GROUP_OK = "LEAVE_GROUP_OK";
    static final String LEAVE_GROUP_ERROR = "LEAVE_GROUP_ERROR";
    static final String DELETE_GROUP_ERROR = "DELETE_GROUP_ERROR";
    static final String DELETE_GROUP_OK = "DELETE_GROUP_OK";

    private static final String URL_GET_MEMBERS = "http://34.69.44.48/instantm/obtener_integrantes.php" ;
    private static final String URL_CREATE_GROUP = "http://34.69.44.48/instantm/crear_grupo.php";
    private static final String URL_GET_GROUPS = "http://34.69.44.48/instantm/obtener_grupos.php";
    private static final String URL_LEAVE_GROUP = "http://34.69.44.48/instantm/abandonar_grupo.php";
    private static final String URL_INSERT_MEMBERS = "http://34.69.44.48/instantm/insertar_integrantes.php";
    private static final String URL_DELETE_GROUP = "http://34.69.44.48/instantm/eliminar_grupo.php";


    public void crateGroup(final Group groupToCreate, final int userId, final String mode) {
        CreateGroupListener createGroupListener = new CreateGroupListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_CREATE_GROUP, createGroupListener, createGroupListener) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(groupToCreate.getId()));
                params.put("mode", mode);
                params.put("name", groupToCreate.getName());
                params.put("user_id", String.valueOf(userId));
                params.put("description", groupToCreate.getDescription());
                params.put("pic", groupToCreate.getPicBLOB());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getUserGroups(final int userId) {
        GetUserGroupsListener getUserGroupsListener = new GetUserGroupsListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_GROUPS, getUserGroupsListener, getUserGroupsListener) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(userId));

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
        System.out.println(id_grupo);

        final String membersToInsert = members;
        System.out.println(membersToInsert);
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

    public void leaveGroup(final int userId, final int groupId) {
            LeaveGroupListener leaveGroupListener = new LeaveGroupListener();
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_LEAVE_GROUP, leaveGroupListener, leaveGroupListener) {

                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("group_id", String.valueOf(groupId));

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, "");

    }

    public void deleteGroup(final int groupId) {
        DeleteGroupListener deleteGroupListener = new DeleteGroupListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_DELETE_GROUP, deleteGroupListener, deleteGroupListener) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_chat_group", String.valueOf(groupId));

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }


    class DeleteGroupListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(DELETE_GROUP_OK, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(DELETE_GROUP_ERROR,"ERROR JSON"));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(DELETE_GROUP_ERROR,"ERROR RESPONSE"));
        }
    }

    class LeaveGroupListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(LEAVE_GROUP_OK, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(LEAVE_GROUP_ERROR,"ERROR JSON"));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(LEAVE_GROUP_ERROR,"ERROR RESPONSE"));
        }
    }


    class GetMembersListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    JSONArray groupsListJSON = jObj.getJSONArray("members");
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
                e.printStackTrace();
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
                    System.out.println(jObj.toString());
                }
            } catch (JSONException e) {
                setChanged();
                e.printStackTrace();
                notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR JSON"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(CREATE_GROUP_ERROR,"ERROR RESPONSE"));
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
}
