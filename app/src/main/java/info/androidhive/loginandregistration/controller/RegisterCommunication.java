package info.androidhive.loginandregistration.controller;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.activity.ChatsActivity;
import info.androidhive.loginandregistration.activity.LoginActivity;

import info.androidhive.loginandregistration.activity.RegisterActivity;
import info.androidhive.loginandregistration.model.Tupla;
import info.androidhive.loginandregistration.model.User;

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
                User user = new User(name, email);

                setChanged();
                notifyObservers(new Tupla<>(OK,  user));
            } else {
                setChanged();
                notifyObservers(new Tupla<>(ERROR, "JSON ERROR"));
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

    public void register(final String username, final String password, final String email) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_REGISTER, this, this){

        protected Map<String, String> getParams() {
            // Parámetros para la consulta POST <columna_db, variables>
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", username);
            params.put("password", password);
            params.put("mail", email);

            return params;
        }
    };

    // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }
}