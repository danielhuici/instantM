package info.androidhive.loginandregistration.chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.contact.ContactCommunication;
import info.androidhive.loginandregistration.group.GroupCommunication;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;
/**
 * Fragmento que muestra los chats.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class TabAFragment extends Fragment implements Observer, AdapterView.OnItemClickListener, TextWatcher {
    private ArrayList<Chat> vChats;
    private ChatAdapter chatsAdapter;
    private SQLiteHandler db;
    private Contact currentSelectedContact;

    private ChatCommunication chatCommunication;

    public TabAFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_a, container, false);
        db = new SQLiteHandler(getActivity());
        vChats = new ArrayList<>();
        chatsAdapter =  new ChatAdapter(super.getActivity(), vChats);
        ListView lvLista = v.findViewById(R.id.lvChats);
        lvLista.setAdapter(chatsAdapter);

        lvLista.setOnItemClickListener(this);

        chatCommunication = new ChatCommunication();
        chatCommunication.addObserver(this);
        getChats();

        EditText etFiltro = v.findViewById(R.id.etSearchChat);
        etFiltro.addTextChangedListener(this);

        return v;
    }

    /**
     * Obtener grupos desde el servidor
     */
    private void getChats() {
        chatCommunication.getUserChats(db.getCurrentID());
    }



    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.GET_USER_GROUPS_OK:

                db.deleteGroups();
                db.addGroups((List<Group>) tupla.b);
                vChats.clear();
                vChats.addAll((List<Group>) tupla.b);
                chatsAdapter.notifyDataSetChanged();
                break;
            case ChatCommunication.GET_CHATS_OK:
                vChats.clear();
                vChats.addAll((List<Chat>) tupla.b);
                chatsAdapter.notifyDataSetChanged();

                break;
            case GroupCommunication.GET_USER_GROUPS_ERROR:
            case ChatCommunication.GET_CHATS_ERROR:
                Toast.makeText(getActivity().getApplicationContext(),
                        (String) tupla.b, Toast.LENGTH_LONG).show();
                break;

            case ContactCommunication.GET_ROOMNAME_OK:
                createMessageActivity((String) tupla.b);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        Chat c = (Chat) chatsAdapter.getItem(i);

        if (c.isGroup) {
            Group g = (Group) chatsAdapter.getItem(i);
            intent.putExtra("groupName",  g.getName());
            intent.putExtra("groupId",  String.valueOf(g.getId()));
            intent.putExtra("group", c);
            startActivityForResult(intent, 2);
        } else {
            currentSelectedContact = (Contact) chatsAdapter.getItem(i);
            ContactCommunication contactCommunication = new ContactCommunication();
            contactCommunication.addObserver(this);
            contactCommunication.getContactRoom(currentSelectedContact.getUserId(), db.getCurrentID());
        }


    }

    /**
     * Abre la ventana de chat.
     * @param roomName nombre de la sala de chat.
     */
    private void createMessageActivity(String roomName) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("roomName",  roomName);
        intent.putExtra("groupId",  "-1");
        intent.putExtra("receiverId",  String.valueOf(currentSelectedContact.getUserId()));
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getChats();

        chatsAdapter.getFilter().filter("");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if(charSequence.length() != 0) {
            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDD " + charSequence);
            chatsAdapter.getFilter().filter(String.valueOf(charSequence));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
