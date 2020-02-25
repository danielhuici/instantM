package info.androidhive.loginandregistration.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
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

public class EditGroupActivity extends AppCompatActivity {

    private EditText inputGroupName;
    private SQLiteHandler db;
    private Button buttonConfirm;
    private ProgressDialog pDialog;
    private final String TAG = "CREATE_GROUP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        inputGroupName = (EditText) findViewById(R.id.group_name);



        // Botón login
        buttonConfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputGroupName.getText().toString().trim();

                // Comprobar que los datos no están vacíos
                if (!name.isEmpty()) {
                    storeGroup(name);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        db = new SQLiteHandler(getApplicationContext());
    }

    private void storeGroup(final String name) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Creando grupo...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_GROUP, new Response.Listener<String>() {

            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Insertar en SQLite

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");


                        db.addGroup(name);

                        Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();

                        // Launch chat activity
                        Intent intent = new Intent(
                                EditGroupActivity.this,
                                Chats.class);
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
