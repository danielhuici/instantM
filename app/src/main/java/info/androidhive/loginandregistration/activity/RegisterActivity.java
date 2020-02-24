package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button buttonRegister;
    private EditText inputUsername;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = (EditText) findViewById(R.id.register_username);
        inputEmail = (EditText) findViewById(R.id.register_email);
        inputPassword = (EditText) findViewById(R.id.register_password);
        inputRepeatPassword = (EditText) findViewById(R.id.register_repeat_password);
        buttonRegister = (Button) findViewById(R.id.btnRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Comprobar si un usuario está ya dentro
        if (session.isLoggedIn()) {
            // Dentro!
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Botón registrarse
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputUsername.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String repeatPassword = inputRepeatPassword.getText().toString().trim();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if(password.equals(repeatPassword)) {
                        registerUser(username, email, password);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "¡Las contrasñas no coinciden!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "¡Introduce los datos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });



    }

    /*
     * Función para registrar al usuario en la base de datos MySQL
     */
    private void registerUser(final String name, final String mail,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando.. ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Store en SQLite

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");

                        // Inserting row in users table
                        db.addUser(name, email);

                        Toast.makeText(getApplicationContext(), "¡Usuario registrado exitosamente!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Ha ocurrido un error
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);
                params.put("mail", mail);

                return params;
            }
        };

        Log.d(TAG, strReq.toString());

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /*
    * Mostrar y ocultar diálogos
    **/

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
