package info.androidhive.loginandregistration.contact;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.utils.Tupla;

public class ContactCommunication extends Observable {

    public static final String CREATE_CONTACT__ERROR = "CREATE_CONTACT__ERROR";
    public static final String CREATE_CONTACT_OK = "CREATE_CONTACT_OK";
    public static final String GET_USER_CONTACTS_ERROR = "GET_USER_CONTACTS_ERROR";
    public static final String GET_USER_CONTACTS_OK = "GET_USER_CONTACTS_OK";
    private static final Object CREATE_CONTACT_ERROR = "CREATE_CONTACT_ERROR";

    public static final String GET_CONTACTS_OK = "GET_CONTACTS_OK";

    public static final String URL_ADD_CONTACT = "http://34.69.44.48/instantm/anadir_contacto.php";
    public static String URL_GET_CONTACTS = "http://34.69.44.48/instantm/obtener_contactos.php";
    public static String URL_GET_USER_CONTACTS = "http://34.69.44.48/instantm/obtener_contactos_usuario.php";


    public void createContact(final String username, final String contact_name) {
        CreateContactListener listener = new CreateContactListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_ADD_CONTACT, listener,listener){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("contact_name", contact_name);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getContacts(final String username) {
        GetContactListener getContactListener = new GetContactListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",username);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getContactsFromUser(final String username) {
        GetUserContactsListener getContactListener = new GetUserContactsListener();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_USER_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }


    class GetContactListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    List<Contact> contacts = Contact.JSONToContact(jObj.getJSONArray("contacts"));
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CONTACTS_OK,contacts));
                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CONTACTS_OK,"ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(GET_CONTACTS_OK,"ERROR"));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_CONTACTS_OK,"ERROR"));
        }
    }



    class CreateContactListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_CONTACT_OK, null));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
        }

    }

    class GetUserContactsListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_CONTACTS_OK, Contact.JSONToContact(jObj.getJSONArray("contacts"))));

                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "JSON ERROR"));
            }
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "RESPONSE ERROR"));
        }
    }
}
