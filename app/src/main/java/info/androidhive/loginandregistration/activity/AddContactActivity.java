package info.androidhive.loginandregistration.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.AppConfig;
import info.androidhive.loginandregistration.controller.AppController;
import info.androidhive.loginandregistration.controller.ContactAdapter;
import info.androidhive.loginandregistration.controller.SQLiteHandler;
import info.androidhive.loginandregistration.model.Contact;


public class AddContactActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {
    private ArrayList<Contact> contacts;
    private ContactAdapter contactAdapter;
    private ListView lvContacts;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contacts = new ArrayList<>();
        contacts.add(new Contact("uno"));
        contacts.add(new Contact("dos"));
        contacts.add(new Contact("tres"));
        contacts.add(new Contact("cuatro"));

        getContactsFromServer();

        contactAdapter =  new ContactAdapter(this, contacts);
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvContacts.setAdapter(contactAdapter);
        lvContacts.setOnItemClickListener(this);
        db = new SQLiteHandler(getApplicationContext());



        EditText filter = (EditText) findViewById(R.id.etSearchContact);
        filter.addTextChangedListener(this);

        }

    private void getContactsFromServer() {
        String tag_string_req = "";
        GetContactListener getContactListener = new GetContactListener();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaa");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {
                System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //TODO Dos opciones, modificar el array que ya tenemos o hacer una consulta al server
             contactAdapter.getFilter().filter(String.valueOf(charSequence));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String contact_name = contactAdapter.getContactName(position);

        String tag_string_req = "";
        CreateContactListener createContactListener = new CreateContactListener();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_CONTACT, createContactListener,createContactListener){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
                params.put("contact_name", contact_name);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    class GetContactListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    // Inserting row in users table
                    contacts.clear();
                    contacts.addAll(Contact.JSONToContact(jObj.getJSONArray("contacts")));
                    contactAdapter.notifyDataSetChanged();
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
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    class CreateContactListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();


                    // Launch chat activity
                   // Intent intent = new Intent(
                      //      AddContactActivity.this,
                        //    ChatsActivity.class);
                    //startActivity(intent);
                    finish();
                } else {
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
