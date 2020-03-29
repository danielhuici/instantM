package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.AppController;
import info.androidhive.loginandregistration.controller.GroupCommunication;
import info.androidhive.loginandregistration.model.Contact;
import info.androidhive.loginandregistration.controller.ContactAdapter;
import info.androidhive.loginandregistration.controller.SQLiteHandler;
import info.androidhive.loginandregistration.model.Tupla;

public class EditGroupActivity extends Activity implements Observer {
    private final String TAG = "CREATE_GROUP";

    private EditText etGroupName;
    private EditText etGroupDescription;
    private Button buttonConfirm;
    private SQLiteHandler db;

    private ArrayList<Contact> members;
    private ContactAdapter contactAdapter;
    private ListView lvMembers;

    private ProgressDialog pDialog;
    private GroupCommunication communication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        etGroupName = (EditText) findViewById(R.id.etGroupName);
        etGroupDescription = (EditText) findViewById(R.id.etGroupDescription);
        buttonConfirm = (Button) findViewById(R.id.btCreateGroup);

        // Botón editar custom_item
        buttonConfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = etGroupName.getText().toString().trim();
                String description = etGroupDescription.getText().toString().trim();
                // Comprobar que los datos no están vacíos
                if (!name.isEmpty()) {
                    storeGroup(name, description);
                    updateGroups();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG).show();
                }
            }

        });

        db = new SQLiteHandler(getApplicationContext());
        communication = new GroupCommunication();
        communication.addObserver(this);

        members = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, members);
        lvMembers = (ListView) findViewById(R.id.listMembers);
        lvMembers.setAdapter(contactAdapter);

        contactAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView, ContactAdapter contactAdapter) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < contactAdapter.getCount(); i++) {
            view = contactAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (contactAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void storeGroup(final String groupName, final String description) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Creando grupo...");
        showDialog();

        communication.crateGroup(groupName, db.getCurrentUsername(), description);

    }

    /**
     * Obtener grupos desde MySQL
     */
    private void getGroups() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Obteniendo información ...");
        showDialog();
        //TODO TO COMMUNICTION METHOD
        GetGroupListener getGroupListener = new GetGroupListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                GroupCommunication.URL_GET_GROUPS,getGroupListener, getGroupListener) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
                Log.v(TAG, db.getCurrentUsername());
                return params;
            }
        };

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

    class GetGroupListener implements Response.Listener<String>, Response.ErrorListener{
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
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.CREATE_GROUP_OK:
                Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();
                finish();
            case GroupCommunication.CREATE_GROUP_ERROR:
                String errorMsg = (String) tupla.b;
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }
}