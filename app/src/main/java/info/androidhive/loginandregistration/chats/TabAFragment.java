package info.androidhive.loginandregistration.chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.group.EditGroupActivity;
import info.androidhive.loginandregistration.group.GroupAdapter;
import info.androidhive.loginandregistration.group.GroupCommunication;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

public class TabAFragment extends Fragment implements Observer, AdapterView.OnItemClickListener {
    private ArrayList<Group> vGroups;
    private GroupAdapter adaptador;
    private ListView lvLista;
    private SQLiteHandler db;

    private final String TAG = "CHATS";
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
        adaptador =  new GroupAdapter(super.getActivity(), vGroups);
        lvLista = (ListView) v.findViewById(R.id.listMembers);
        lvLista.setAdapter(adaptador);

        lvLista.setOnItemClickListener(this);
        communication = new GroupCommunication();
        communication.addObserver(this);
        getGroups();
        return v;
    }


    /**
     * Obtener grupos desde MySQL
     * */
    private void getGroups() {
        communication.getUserGroups(db.getCurrentUsername());
    }



    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.GET_USER_GROUPS_OK:

                db.deleteGroups();
                db.addGroups((List<Group>) tupla.b);
                vGroups.clear();
                vGroups.addAll(db.getGroups());
                adaptador.notifyDataSetChanged();
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
        intent.putExtra("group",  (Group) adaptador.getItem(i));
        startActivityForResult(intent, 2);//(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getGroups();
    }
}
