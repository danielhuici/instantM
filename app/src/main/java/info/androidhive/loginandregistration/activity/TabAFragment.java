package info.androidhive.loginandregistration.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.GroupAdapter;
import info.androidhive.loginandregistration.app.Grupo;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

public class TabAFragment extends Fragment {
    private ArrayList<Grupo> vGrupos;
    private GroupAdapter adaptador;
    private ListView lvLista;
    private SQLiteHandler db;

    public TabAFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_a, container, false);
        vGrupos = new ArrayList<>();
        adaptador =  new GroupAdapter(super.getActivity(), showGroups());
        lvLista = (ListView) v.findViewById(R.id.listaGrupos);
        lvLista.setAdapter(adaptador);

        adaptador.notifyDataSetChanged();

        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("GRUPO:", "Tocado" + position);
            }
        });

        return v;
    }

    private ArrayList<Grupo> showGroups() {
        List<String> groups;
        groups = db.getGroups();
        ArrayList<Grupo> vGrupos = new ArrayList<>();

        for (String group : groups) {
            try {
                vGrupos.add(new Grupo(group, "07-03-2020"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return vGrupos;
    }

}
