package info.androidhive.loginandregistration.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.model.User;
import info.androidhive.loginandregistration.model.UserAdapter;

public class AddContact extends AppCompatActivity implements TextWatcher{
    private ArrayList<User> contacts;
    private UserAdapter userAdapter;
    private ListView lvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contacts = new ArrayList<>();
        userAdapter =  new UserAdapter(this, contacts);
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvContacts.setAdapter(userAdapter);


        try {
            //TODO esto va a un método de conexion con el servidor, se cargará siempre desde el servidor
            contacts.add(new User("Juan","15-10-96 17:00"));
            contacts.add(new User("Mauricio","15-10-96 17:00"));
            contacts.add(new User("Paloma","15-10-96 17:00"));
            contacts.add(new User("Carlos","15-10-96 17:00"));
            contacts.add(new User("Fernando","15-10-96 17:00"));
            contacts.add(new User("Alicia","15-10-96 17:00"));
            contacts.add(new User("Roberto","15-10-96 17:00"));
            contacts.add(new User("Marisa","15-10-96 17:00"));
            contacts.add(new User("Concho","15-10-96 17:00"));
            contacts.add(new User("Concha","15-10-96 17:00"));
            contacts.add(new User("",""));
            userAdapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        EditText filter = (EditText) findViewById(R.id.etSearchContact);
        filter.addTextChangedListener(this);

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //TODO Dos opciones, modificar el array que ya tenemos o hacer una consulta al server
             userAdapter.getFilter().filter(String.valueOf(charSequence));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
}
