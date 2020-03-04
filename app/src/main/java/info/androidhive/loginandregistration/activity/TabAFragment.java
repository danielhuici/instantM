package info.androidhive.loginandregistration.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.app.GroupAdapter;
import info.androidhive.loginandregistration.app.Grupo;
import info.androidhive.loginandregistration.helper.SessionManager;

public class TabAFragment extends Fragment {
    private ArrayList<Grupo> vGrupos;
    private GroupAdapter adaptador;
    private ListView lvLista;
    private ProgressDialog pDialog;
    private SessionManager session;

    public TabAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Session manager
       // session = new SessionManager(getApplicationContext());

        View v = inflater.inflate(R.layout.fragment_tab_a, container, false);
        vGrupos = new ArrayList<>();
        adaptador =  new GroupAdapter(super.getActivity(), vGrupos);
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
        vGrupos.add(new Grupo("Concho","Ult Con: 14:00"));
        vGrupos.add(new Grupo("",""));

        adaptador.notifyDataSetChanged();
        return v;
    }

    /**
     * Comprobar datos conrrectos con MySQL
     * *
    private void getGroups(final int idUser) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Entrando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // JSON error node?
                    if (!error) { // No hay error
                        session.setLogin(true);

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("mail");

                        // Inserting row in users table
                        db.addUser(name, email);

                        // Lanzar Main Activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else { // Error
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error. No debería venir nunca aquí
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", idUser);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
     */

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
