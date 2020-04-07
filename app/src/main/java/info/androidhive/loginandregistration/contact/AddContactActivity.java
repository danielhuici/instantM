package info.androidhive.loginandregistration.contact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;


public class AddContactActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener, Observer {
    private ArrayList<Contact> contacts;
    private ContactAdapter contactAdapter;
    private ListView lvContacts;
    private SQLiteHandler db;
    private ContactCommunication communication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contacts = new ArrayList<>();
        db = new SQLiteHandler(getApplicationContext());


        communication = new ContactCommunication();
        communication.addObserver(this);

        getContactsFromServer();

        contactAdapter =  new ContactAdapter(this, contacts);
        lvContacts = findViewById(R.id.lvContacts);
        lvContacts.setAdapter(contactAdapter);
        lvContacts.setOnItemClickListener(this);

        EditText etFiltro = findViewById(R.id.etSearchContact);
        etFiltro.addTextChangedListener(this);

    }

    private void getContactsFromServer() {
        communication.getContacts(db.getCurrentID());

    }

    @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             contactAdapter.getFilter().filter(String.valueOf(charSequence));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int contactId = contactAdapter.getSelectedConctactId(position);

        Bundle extra=getIntent().getExtras();
        System.out.println("UUUNO");
        if(extra == null) {
            communication.createContact(db.getCurrentID(), contactId);
            System.out.println("DOS");
        }else{
            System.out.println("TRES");
            Intent output = new Intent();
            output.putExtra("contact_name", contactAdapter.getContactName(position));
            output.putExtra("contact_id", contactId);
            setResult(RESULT_OK, output);
            finish();
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case ContactCommunication.GET_CONTACTS_OK:
                contacts.clear();
                contacts.addAll((List<Contact>) tupla.b);
                contactAdapter.notifyDataSetChanged();
                break;
            case ContactCommunication.CREATE_CONTACT_OK:
                Toast.makeText(getApplicationContext(), "¡Contacto creado exitosamente!", Toast.LENGTH_LONG).show();
                finish();
                break;
            case ContactCommunication.CREATE_CONTACT__ERROR:
                Toast.makeText(getApplicationContext(),
                        (String) tupla.b, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
