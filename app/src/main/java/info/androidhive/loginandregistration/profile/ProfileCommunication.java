package info.androidhive.loginandregistration.profile;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.session.User;
import info.androidhive.loginandregistration.utils.Tupla;

class ProfileCommunication extends Observable implements Response.Listener<String>, Response.ErrorListener {
    private static final String PROFILE_UPDATE = "http://34.69.44.48/instantm/actualizar_perfil.php";
    protected static final String UPDATE_OK = "UPDATE_OK";
    protected static final String UPDATE_ERROR = "UPDATE_ERROR";

    public void updateProfile(final User user) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                PROFILE_UPDATE, this, this) {

            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", user.getUsername());
                if (user.getPassword() != null)
                    params.put("password", user.getPassword());
                params.put("mail", user.getEmail());
                params.put("state", user.getState());
                params.put("birthday", new SimpleDateFormat("yyyy-mm-dd", Locale.GERMANY).format(user.getBirthdate()));
                params.put("id_user", String.valueOf(user.getId()));


                System.out.println(user.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }



        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    /*JSONObject jsonResponse = jObj.getJSONObject("user");
                    String name = jsonResponse.getString("name");
                    String email = jsonResponse.getString("mail");
                    int id = jsonResponse.getInt("id_user");
                    User user = new User(name, email, id);*/

                    setChanged();
                    notifyObservers(new Tupla<>(UPDATE_OK,  /*user*/ null));
                } else {
                    System.out.println(jObj.toString());
                    setChanged();
                    notifyObservers(new Tupla<>(UPDATE_ERROR, "JSON RESPONSE"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(UPDATE_ERROR, "JSON ERROR"));
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(UPDATE_ERROR,error.getMessage()));
        }
}
