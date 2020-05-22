package info.androidhive.loginandregistration.session;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.utils.Tupla;

public class LoginCommunication extends Observable implements Response.Listener<String>, Response.ErrorListener {
    public static final String ERROR = "ERROR";
    public static final String OK = "OK";
    public static String URL_LOGIN = "http://34.69.44.48/instantm/login.php";


    @Override
    public void onResponse(String response) {
        try {
            JSONObject jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");

            // JSON error node?
            if (!error) { // No hay error

                JSONObject user = jObj.getJSONObject("user");
                int id = user.getInt("id_user");
                User u = new User();
                u.setId(id);
                u.setUsername(user.getString("name"));
                u.setEmail(user.getString("mail"));
                //u.setState(user.getString("state"));
                u.setState("TEST");
                if(user.getString("birthday") != null)
                u.setBirthday(user.getString("birthday"));
                setChanged();
                notifyObservers(new Tupla<>(OK, u));
            } else { // Error
                String errorMsg = jObj.getString("error_msg");
                setChanged();
                notifyObservers(new Tupla<>(ERROR,errorMsg));

            }
        } catch (JSONException | ParseException e) {
            // JSON error. No debería venir nunca aquí
            e.printStackTrace();
            setChanged();
            notifyObservers(new Tupla<>(ERROR,"Json error: " + e.getMessage()));
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        notifyObservers(new Tupla<>(ERROR,error.getMessage()));
    }

    public void login(final String username, final String password ) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_LOGIN, this, this) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("password", password);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "req_login");
    }
}
