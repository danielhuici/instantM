package info.androidhive.loginandregistration.session;


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
import info.androidhive.loginandregistration.utils.Tupla;

public class RegisterCommunication extends Observable implements Response.Listener<String>, Response.ErrorListener {
    public static final String ERROR = "ERROR";
    public static final String OK = "OK";
    public static String URL_REGISTER = "http://34.69.44.48/instantm/registrar.php";

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");
            if (!error) {
                JSONObject jsonResponse = jObj.getJSONObject("user");
                String name = jsonResponse.getString("name");
                String email = jsonResponse.getString("mail");
                int id = jsonResponse.getInt("id_user");
                User user = new User(name, email, id);

                setChanged();
                notifyObservers(new Tupla<>(OK,  user));
            } else {
                System.out.println(jObj.toString());
                setChanged();
                notifyObservers(new Tupla<>(ERROR, "JSON RESPONSE"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            setChanged();
            notifyObservers(new Tupla<>(ERROR, "JSON ERROR"));
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setChanged();
        notifyObservers(new Tupla<>(ERROR,error.getMessage()));
    }

    public void register(final User user) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_REGISTER, this, this){

        protected Map<String, String> getParams() {
            // Parámetros para la consulta POST <columna_db, variables>
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", user.getUsername());
            params.put("password", user.getPassword());
            params.put("mail", user.getEmail());
            params.put("birthday", new SimpleDateFormat("yyyy-mm-dd", Locale.GERMANY).format(user.getBirthdate()));
            return params;
        }
    };

    // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }
}