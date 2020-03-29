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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.AppConfig;
import info.androidhive.loginandregistration.controller.AppController;
import info.androidhive.loginandregistration.controller.ContactAdapter;
import info.androidhive.loginandregistration.controller.ContactCommunication;
import info.androidhive.loginandregistration.controller.SQLiteHandler;

import info.androidhive.loginandregistration.model.Contact;
import info.androidhive.loginandregistration.model.Tupla;

public class TabBFragment extends Fragment implements Observer {
    private ArrayList<Contact> vContacts;
    private ContactAdapter contactAdapter;
    private ListView lvLista;
    private SQLiteHandler db;
    Activity mActivity;

    ContactCommunication communication;
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

        communication = new ContactCommunication();
        communication.addObserver(this);
        getContactsFromServer();
        vContacts.indexOf(1);


        contactAdapter.notifyDataSetChanged();
        return v;
    }

    private void getContactsFromServer() {
        String tag_string_req = "";

        communication.getContactsFromUser(db.getCurrentUsername());

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

    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case ContactCommunication.GET_USER_CONTACTS_OK:
                vContacts.clear();
                vContacts.addAll((List<Contact>) tupla.b);
                contactAdapter.notifyDataSetChanged();
                break;
            case ContactCommunication.GET_USER_CONTACTS_ERROR:
                String errorMsg = (String) tupla.b;
                Toast.makeText(getActivity().getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }
}


