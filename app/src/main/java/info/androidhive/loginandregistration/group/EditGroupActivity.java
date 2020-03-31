package info.androidhive.loginandregistration.group;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.contact.ContactCommunication;
import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.contact.ContactAdapter;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

public class EditGroupActivity extends Activity implements Observer, View.OnClickListener {
    private final String TAG = "CREATE_GROUP";

    private EditText etGroupName;
    private EditText etGroupDescription;
    private Button btnEditGroup;
    private Button btnOutGroup;
    private Button btnDeleteGroup;
    private SQLiteHandler db;

    private ArrayList<Contact> members;
    private ContactAdapter contactAdapter;
    private ListView lvMembers;
    private ImageView ivProfile;
    private ProgressDialog pDialog;
    private GroupCommunication communication;
    private ContactCommunication contactCommunication;

    private int idGroup;
    private Group groupToUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Bundle data = this.getIntent().getExtras();
        idGroup = data.getInt("idGroup");

        groupToUpdate = getGroup(idGroup);

        etGroupName = (EditText) findViewById(R.id.group_name);
        etGroupDescription = (EditText) findViewById(R.id.group_description);
        btnEditGroup = (Button) findViewById(R.id.btn_edit_group);
        btnOutGroup = (Button) findViewById(R.id.btn_out_group);
        btnDeleteGroup = (Button) findViewById(R.id.btn_delete_group);

        ivProfile = (ImageView) findViewById(R.id.ivGroupImage);
        ivProfile.setOnClickListener(this);

        // Botón editar custom_item
        btnEditGroup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                groupToUpdate.setName(etGroupName.getText().toString().trim());
                groupToUpdate.setDescription(etGroupDescription.getText().toString().trim());
                // Comprobar que los datos no están vacíos
                if (!etGroupName.getText().toString().trim().isEmpty()) {
                    updateGroup(groupToUpdate);
                }else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Botón salir el usuario del grupo
        btnOutGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Quiero salir del grupo");
            }
        });

        //Botón borrar el grupo
        btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Quiero borrar el grupo");
            }
        });

        db = new SQLiteHandler(getApplicationContext());
        communication = new GroupCommunication();
        communication.addObserver(this);

        members = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, members);
        lvMembers = (ListView) findViewById(R.id.listMembers);
        lvMembers.setAdapter(contactAdapter);

        contactAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView, ContactAdapter contactAdapter) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < contactAdapter.getCount(); i++) {
            view = contactAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (contactAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /*
    * Obtener los datos del grupo a modificar.
     */
    private Group getGroup(int id) {
        // Tag used to cancel the request
        String tag_string_req = "req_get";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Obteniendo grupo...");
        showDialog();

        //communication.updateGroup(groupToUpdate, db.getCurrentUsername());
    }

    /*
    * Modificar grupo desde mysql
     */
    private void updateGroup(Group groupToUpdate) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Modificando grupo...");
        showDialog();

        return communication.updateGroup(groupToUpdate, idGroup);

    }

    /**
     * Obtener grupos desde MySQL
     */
    private void getGroups() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Obteniendo información ...");
        showDialog();
        GetGroupListener getGroupListener = new GetGroupListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                GroupCommunication.URL_GET_GROUPS,getGroupListener, getGroupListener) {
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", db.getCurrentUsername());
                Log.v(TAG, db.getCurrentUsername());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void updateGroups() {
        db.deleteGroups();
        getGroups();
    }

    /*
     * Mostrar y ocultar diálogos
     **/

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Bitmap bitmap = decodeUri(data.getData());
        ivProfile.setImageBitmap(bitmap);
        groupToUpdate.setPic(bitmap);

    }
    class GetGroupListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            hideDialog();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    JSONArray groups = jObj.getJSONArray("groups");

                    // Inserting row in users table
                    db.addGroups(groups);
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
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.CREATE_GROUP_OK:
                Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();
                finish();
                break;
            case GroupCommunication.CREATE_GROUP_ERROR:
                String errorMsg = (String) tupla.b;
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public Bitmap decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFileDescriptor(imageSource, null, o2);


        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
        return null;
    }

}