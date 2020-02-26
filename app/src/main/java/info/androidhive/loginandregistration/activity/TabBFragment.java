package info.androidhive.loginandregistration.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.GroupAdapter;
import info.androidhive.loginandregistration.app.Grupo;

public class TabBFragment extends Fragment {
    private ArrayList<Grupo> vGrupos;
    private GroupAdapter adaptador;
    private ListView lvLista;
    
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
        vGrupos = new ArrayList<>();
        adaptador =  new GroupAdapter(super.getActivity(), vGrupos) ;

        lvLista = (ListView) v.findViewById(R.id.listaGrupos);
        lvLista.setAdapter(adaptador);

        vGrupos.add(new Grupo("Juan","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Mauricio","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Paloma","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Carlos","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Fernando","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Alicia","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Roberto","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Marisa","Ult Con: 14:00"));
        vGrupos.add(new Grupo("Concha","Ult Con: 14:00"));
        vGrupos.add(new Grupo("",""));

        vGrupos.indexOf(1);


        adaptador.notifyDataSetChanged();
        return v;
    }
}


