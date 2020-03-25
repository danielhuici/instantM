package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.AppConfig;
import info.androidhive.loginandregistration.controller.AppController;
import info.androidhive.loginandregistration.controller.ContactAdapter;
import info.androidhive.loginandregistration.controller.SQLiteHandler;

import info.androidhive.loginandregistration.model.Contact;

public class TabBFragment extends Fragment {
    private ArrayList<Contact> vContacts;
    private ContactAdapter contactAdapter;
    private ListView lvLista;
    private SQLiteHandler db;
    Activity mActivity;
    public TabBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_a, container, false);
        db = new SQLiteHandler(super.getActivity());
        vContacts = showContacts();
        contactAdapter =  new ContactAdapter(super.getActivity(), vContacts) ;

        lvLista = (ListView) v.findViewById(R.id.listMembers);
        lvLista.setAdapter(contactAdapter);
        vContacts.add(new Contact("uno"));
        vContacts.add(new Contact("uno"));
        vContacts.add(new Contact("dos"));
        vContacts.add(new Contact("tres"));
        vContacts.add(new Contact("cuatro"));
        getContactsFromServer();
        vContacts.indexOf(1);


        contactAdapter.notifyDataSetChanged();
        return v;
    }

    private void getContactsFromServer() {
        String tag_string_req = "";
        GetContactListener getContactListener = new GetContactListener();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_USER_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {

                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
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
                    vContacts.clear();
                    vContacts.addAll(Contact.JSONToContact(jObj.getJSONArray("contacts")));
                    contactAdapter.notifyDataSetChanged();
                } else { // Error
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getActivity().getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error. No debería venir nunca aquí
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity().getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private ArrayList<Contact> showContacts() {
        List<String> contacts;
        contacts = db.getContacts();

        ArrayList<Contact> vContacts = new ArrayList<>();

        for (String contact : contacts) {
                vContacts.add(new Contact(contact));
        }
        return vContacts;
    }
}


