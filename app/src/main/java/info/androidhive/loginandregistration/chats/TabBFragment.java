package info.androidhive.loginandregistration.chats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.ContactAdapter;
import info.androidhive.loginandregistration.contact.ContactCommunication;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.utils.SQLiteHandler;

import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.utils.Tupla;
/**
 * Fragmento que muestra los contactos de un usuario
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class TabBFragment extends Fragment implements Observer, AdapterView.OnItemClickListener {
    private ArrayList<Contact> vContacts;
    private ContactAdapter contactAdapter;
    private ListView lvLista;
    private SQLiteHandler db;
    private Contact currentSelectedContact;
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
        View v = inflater.inflate(R.layout.fragment_tab_b, container, false);
        db = new SQLiteHandler(super.getActivity());
        vContacts = showContacts();

        contactAdapter =  new ContactAdapter(super.getActivity(), vContacts) ;

        lvLista = v.findViewById(R.id.lvContacts);
        lvLista.setAdapter(contactAdapter);

        lvLista.setOnItemClickListener(this);

        communication = new ContactCommunication();
        communication.addObserver(this);
        getContactsFromServer();
        vContacts.indexOf(1);
        registerForContextMenu(lvLista);

        return v;
    }

    private void getContactsFromServer() {
        communication.getContactsFromUser(db.getCurrentID());
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
            case ContactCommunication.DELETE_CONTACT_OK:
                getContactsFromServer();
                break;
            case ContactCommunication.DELETE_CONTACT_ERROR:
                break;
            case ContactCommunication.GET_ROOMNAME_OK:
                createMessageActivity((String) tupla.b);
                break;
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu_contact, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int itemSeleccionado = info.position;

        switch (item.getItemId()) {
            case R.id.action_eliminar_contact:
                communication.deleteContact(db.getCurrentUsername(), (Contact) contactAdapter.getItem(itemSeleccionado));
                break;

        }
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        currentSelectedContact = (Contact) contactAdapter.getItem(i);
        communication.getContactRoom(currentSelectedContact.getUserId(), db.getCurrentID());
    }

    private void createMessageActivity(String roomName) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("roomName",  roomName);
        intent.putExtra("groupId",  "-1");
        intent.putExtra("receiverId",  String.valueOf(currentSelectedContact.getUserId()));
        startActivityForResult(intent, 2);
    }
}


