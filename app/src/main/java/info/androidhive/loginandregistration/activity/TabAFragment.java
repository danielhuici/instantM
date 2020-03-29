package info.androidhive.loginandregistration.activity;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.AppController;
import info.androidhive.loginandregistration.controller.GroupAdapter;
import info.androidhive.loginandregistration.controller.GroupCommunication;
import info.androidhive.loginandregistration.model.Group;
import info.androidhive.loginandregistration.controller.SQLiteHandler;
import info.androidhive.loginandregistration.model.Tupla;

public class TabAFragment extends Fragment implements Observer {
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

        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("GRUPO:", "Tocado" + position);
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });
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

    private ArrayList<Group> loadGroupsToLocalDB() {
        List<String> groups;
        groups = db.getGroups();
        ArrayList<Group> vGroups = new ArrayList<>();

        for (String group : groups) {
                vGroups.add(new Group(group, "07-03-2020"));
        }
        return vGroups;
    }

    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.GET_USER_GROUPS_OK:

                db.deleteGroups();
                db.addGroups((List<Group>) tupla.b);
                vGroups.clear();
                vGroups.addAll(loadGroupsToLocalDB());
                adaptador.notifyDataSetChanged();

                break;
            case GroupCommunication.GET_USER_GROUPS_ERROR:
                Toast.makeText(getActivity().getApplicationContext(),
                        (String) tupla.b, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
