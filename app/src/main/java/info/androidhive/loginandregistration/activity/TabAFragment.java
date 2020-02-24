package info.androidhive.loginandregistration.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.GroupAdapter;
import info.androidhive.loginandregistration.app.Grupo;

public class TabAFragment extends Fragment {
    private ArrayList<Grupo> vGrupos;
    private GroupAdapter adaptador;
    private ListView lvLista;

    public TabAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*vGrupos = new ArrayList<>();
        adaptador =  new GroupAdapter(super.getActivity(), vGrupos);
        lvLista = (ListView) getView().findViewById(R.id.listaGrupos);
        lvLista.setAdapter(adaptador);
        Grupo g = new Grupo("Martin","Gascón");
        vGrupos.add(g);
        vGrupos.add(g);
        vGrupos.add(g);
        vGrupos.add(g);
        vGrupos.add(g);vGrupos.add(g);vGrupos.add(g);vGrupos.add(g);vGrupos.add(g);vGrupos.add(g);

        adaptador.notifyDataSetChanged();*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_a, container, false);
    }
}
