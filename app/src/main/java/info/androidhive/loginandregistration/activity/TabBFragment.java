package info.androidhive.loginandregistration.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.ParseException;
import java.util.ArrayList;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.model.GroupAdapter;
import info.androidhive.loginandregistration.model.Grupo;

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

        lvLista = (ListView) v.findViewById(R.id.listMembers);
        lvLista.setAdapter(adaptador);
        try {
            vGrupos.add(new Grupo("Juan","15-10-96 17:00"));
            vGrupos.add(new Grupo("Mauricio","15-10-96 17:00"));
            vGrupos.add(new Grupo("Paloma","15-10-96 17:00"));
            vGrupos.add(new Grupo("Carlos","15-10-96 17:00"));
            vGrupos.add(new Grupo("Fernando","15-10-96 17:00"));
            vGrupos.add(new Grupo("Alicia","15-10-96 17:00"));
            vGrupos.add(new Grupo("Roberto","15-10-96 17:00"));
            vGrupos.add(new Grupo("Marisa","15-10-96 17:00"));
            vGrupos.add(new Grupo("Concho","15-10-96 17:00"));
            vGrupos.add(new Grupo("",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        vGrupos.indexOf(1);


        adaptador.notifyDataSetChanged();
        return v;
    }
}


