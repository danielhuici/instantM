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

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.app.GroupAdapter;
import info.androidhive.loginandregistration.app.Grupo;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

public class TabAFragment extends Fragment {
    private ArrayList<Grupo> vGrupos;
    private GroupAdapter adaptador;
    private ListView lvLista;
    private SQLiteHandler db;

    private final String TAG = "CHATS";

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

        return v;
    }


    private void getGroups(final int id_creator_user) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RETRIVE_GROUPS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Chats response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // JSON error node?
                    if (!error) { // No hay error
                        JSONArray groupsJson = jObj.getJSONArray("groups");
                        List<String> groupList = new ArrayList<String>();

                        for (int i = 0; i < groupsJson.length(); i++) {
                            groupList.add(groupsJson.getString(i));
                        }

                        Log.v(TAG, groupList.toString());

                        // Inserting row in users table
                        // db.addGroup(name);

                    } else { // Error
                        String errorMsg = jObj.getString("error_msg");
                        Log.v(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error. No debería venir nunca aquí
                    e.printStackTrace();
                    Log.v(TAG, "Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_creator_user", String.valueOf(id_creator_user));

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

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
