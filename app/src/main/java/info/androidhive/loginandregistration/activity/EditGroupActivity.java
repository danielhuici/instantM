package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.app.User;
import info.androidhive.loginandregistration.app.UserAdapter;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

public class EditGroupActivity extends Activity {
    private final String TAG = "CREATE_GROUP";

    private EditText inputGroupName;
    private Button buttonConfirm;
    private SQLiteHandler db;

    private ArrayList<User> members;
    private UserAdapter userAdapter;
    private ListView lvMembers;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

       inputGroupName = (EditText) findViewById(R.id.group_name);
       buttonConfirm =  (Button) findViewById(R.id.btn_create_group);

        // Botón editar custom_item
        buttonConfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputGroupName.getText().toString().trim();

                // Comprobar que los datos no están vacíos
                if (!name.isEmpty()) {
                    storeGroup(name);
                    updateGroups();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG).show();
                }
            }

        });

        db = new SQLiteHandler(getApplicationContext());

        members = new ArrayList<>();
        userAdapter =  new UserAdapter(this, members);
        lvMembers = (ListView) findViewById(R.id.listMembers);
        lvMembers.setAdapter(userAdapter);

        try {
            members.add(new User("Juan","15-10-96 17:00"));
            members.add(new User("Mauricio","15-10-96 17:00"));
            members.add(new User("Paloma","15-10-96 17:00"));
            members.add(new User("Carlos","15-10-96 17:00"));
            members.add(new User("Fernando","15-10-96 17:00"));
            members.add(new User("Alicia","15-10-96 17:00"));
            members.add(new User("Roberto","15-10-96 17:00"));
            members.add(new User("Marisa","15-10-96 17:00"));
            members.add(new User("Concho","15-10-96 17:00"));
            members.add(new User("",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        userAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lvMembers, userAdapter);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView, UserAdapter userAdapter) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
        View.MeasureSpec.UNSPECIFIED);int totalHeight = 0;
        View view = null;
        for (int i = 0; i < userAdapter.getCount(); i++) {
            view = userAdapter.getView(i, view, listView);if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (userAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void storeGroup(final String name) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Creando grupo...");
        showDialog();

        final String username =  db.getCurrentUsername();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_GROUP, new Response.Listener<String>() {

            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
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
                params.put("username", username);

                return params;
            }
        };

        Log.d(TAG, strReq.toString());

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    /**
     * Obtener grupos desde MySQL
     * */
    private void getGroups() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Obteniendo información ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_GROUPS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // JSON error node?
                    if (!error) { // No hay error
                        JSONArray groups = jObj.getJSONArray("groups");

                        // Inserting row in users table
                        db.addGroups(groups);
                    } else { // Error
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error. No debería venir nunca aquí
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
                Log.v(TAG, db.getCurrentUsername());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateGroups() {
        db.deleteGroups();
        getGroups();
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
