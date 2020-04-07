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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.group.GroupAdapter;
import info.androidhive.loginandregistration.group.GroupCommunication;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

public class TabAFragment extends Fragment implements Observer, AdapterView.OnItemClickListener, TextWatcher {
    private ArrayList<Group> vGroups;
    private GroupAdapter groupAdapter;
    private ListView lvLista;
    private SQLiteHandler db;

    private GroupCommunication communication;

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
        vGroups = new ArrayList<>();
        groupAdapter =  new GroupAdapter(super.getActivity(), vGroups);
        lvLista = v.findViewById(R.id.lvChats);
        lvLista.setAdapter(groupAdapter);

        lvLista.setOnItemClickListener(this);

        communication = new GroupCommunication();
        communication.addObserver(this);
        getGroups();

        EditText etFiltro = v.findViewById(R.id.etSearchChat);
        etFiltro.addTextChangedListener(this);
        return v;
    }


    /**
     * Obtener grupos desde MySQL
     * */
    private void getGroups() {
        communication.getUserGroups(db.getCurrentID());
    }



    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.GET_USER_GROUPS_OK:

                db.deleteGroups();
                db.addGroups((List<Group>) tupla.b);
                vGroups.clear();
                vGroups.addAll((List<Group>) tupla.b);
                groupAdapter.notifyDataSetChanged();
                break;
            case GroupCommunication.GET_USER_GROUPS_ERROR:
                Toast.makeText(getActivity().getApplicationContext(),
                        (String) tupla.b, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("group",  (Group) groupAdapter.getItem(i));
        startActivityForResult(intent, 2);//(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getGroups();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        groupAdapter.getFilter().filter(String.valueOf(charSequence));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
