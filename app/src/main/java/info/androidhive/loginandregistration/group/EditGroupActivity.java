package info.androidhive.loginandregistration.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.AddContactActivity;
import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.contact.ContactAdapter;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;
/**
 * Clase controladora de la ventana de edición y visualización de un grupo.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class EditGroupActivity extends Activity implements Observer, View.OnClickListener, AdapterView.OnItemClickListener {
    private final String TAG = "CREATE_GROUP";

    private Button btEditGroup;
    private Button btDeleteGroup;


    private SQLiteHandler db;
    private TextView tvLeaveGroup;
    private ArrayList<Contact> vMembers;
    private ContactAdapter contactAdapter;
    private ListView lvMembers;
    private ImageView ivProfile;
    private ImageView ivEditGroupName;
    private ImageView ivEditGroupDescription;

    private TextView tvGroupName;
    private TextView tvGroupDescription;

    private GroupCommunication communication;
    private Bundle extra;
    private Group groupToUpdate;

    private String save_mode = "OVERWRITE";
    private final String WRITE_MODE = "WRITE";
    private final String OVERWRITE_MODE = "OVERWRITE";

    private String activityMode ;
    private final String CREATE_MODE = "CREATE_MODE";
    private final String MODIFY_MODE = "MODIFY_MODE";
    private final String VIEW_MODE = "VIEW_MODE";
    private final int IMAGE_PICKER_REQUEST_CODE = 0;
    private final int NAME_PICKER_REQUEST_CODE = 1;
    private final int CONTACT_PICKER_REQUEST_CODE = 2;
    private final int DESCRIPTION_PICKER_REQUEST_CODE = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        db = new SQLiteHandler(getApplicationContext());
        initComponents();

        communication = new GroupCommunication();
        communication.addObserver(this);

        groupToUpdate = new Group();

        addListeners();
        loadGroup();

    }

    /**
     * añade escuchadores a los componentes de la interfaz.
     */
    private void addListeners() {
        btEditGroup.setOnClickListener(this);
        tvLeaveGroup.setOnClickListener(this);
        btDeleteGroup.setOnClickListener(this);
        tvGroupName.setOnClickListener(this);
        tvGroupDescription.setOnClickListener(this);
        if(!activityMode.equals(VIEW_MODE))
            ivProfile.setOnClickListener(this);
        ivEditGroupName.setOnClickListener(this);
        ivEditGroupDescription.setOnClickListener(this);
        lvMembers.setOnItemClickListener(this);
    }

    /**
     * Inicializa los componentes.
     */
    private void initComponents() {
        btEditGroup = findViewById(R.id.btEditGroup);

        btDeleteGroup = findViewById(R.id.btDeleteGroup);
        tvLeaveGroup = findViewById(R.id.tvLeaveGroup);
        ivProfile = findViewById(R.id.ivGroupImage);
;
        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);

        ivEditGroupName = findViewById(R.id.ivEditGroupName);
        ivEditGroupDescription = findViewById(R.id.ivEditGroupDescription);

        vMembers = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, vMembers);
        lvMembers = findViewById(R.id.lvMembers);
        lvMembers.setAdapter(contactAdapter);

        if(getIntent().getExtras() != null) {
            extra = getIntent().getExtras();
            groupToUpdate = (Group) extra.getSerializable("group");

            if (groupToUpdate.isAdmin(db.getCurrentID())) { //MODIFY MODE WITH ADMIN PRIVILEGES
                save_mode = OVERWRITE_MODE;
                activityMode = MODIFY_MODE;
                vMembers.add(new Contact("-1"));
            }else{                                          //VIEW MODE -> CAN`t EDIT
                btEditGroup.setVisibility(View.GONE);
                btDeleteGroup.setVisibility(View.GONE);
                ivEditGroupDescription.setVisibility(View.GONE);
                ivEditGroupName.setVisibility(View.GONE);
                activityMode = VIEW_MODE;
            }
        }else{                                              //CREATE_MODE
            vMembers.add(new Contact("-1"));
            save_mode = WRITE_MODE;
            activityMode = CREATE_MODE;
            btDeleteGroup.setVisibility(View.GONE);
            tvLeaveGroup.setVisibility(View.GONE);
            tvLeaveGroup.setVisibility(View.GONE);
            vMembers.add(new Contact(db.getCurrentUsername(), db.getCurrentID()));
        }
        contactAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
    }

    /**
     * Carga la información del grupo.
     */
    private void loadGroup() {
        extra = getIntent().getExtras();

        if(extra != null) {
                groupToUpdate = (Group) extra.getSerializable("group");
                tvGroupName.setText(String.valueOf(groupToUpdate.getName()));
                if(!groupToUpdate.getDescription().equalsIgnoreCase("null") &&
                        groupToUpdate.getDescription() != null) {
                    tvGroupDescription.setText(groupToUpdate.getDescription());
                }
                tvGroupName.setText(String.valueOf(groupToUpdate.getName()));
                communication.getMembers(groupToUpdate.getId());
        }
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ivGroupImage:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
                break;
            case R.id.btEditGroup:
                    vMembers.remove(0);
                    groupToUpdate.setName(tvGroupName.getText().toString());
                    groupToUpdate.setDescription(tvGroupDescription.getText().toString());
                    communication.crateGroup(groupToUpdate, db.getCurrentID(), save_mode);
                break;
            case R.id.tvLeaveGroup:
                communication.leaveGroup(db.getCurrentID(), groupToUpdate.getId());
                break;
            case R.id.ivEditGroupName:
                Intent intent1 = new Intent(this,
                        GroupNamePicker.class);
                intent1.putExtra("name", tvGroupName.getText().toString());
                startActivityForResult(intent1, NAME_PICKER_REQUEST_CODE);
                break;
            case R.id.ivEditGroupDescription:
                Intent intent2 = new Intent(this,
                        GroupDescriptionPicker.class);
                intent2.putExtra("description",tvGroupDescription.getText());
                startActivityForResult(intent2, DESCRIPTION_PICKER_REQUEST_CODE);
                break;
            case R.id.btDeleteGroup:
                communication.deleteGroup(groupToUpdate.getId());

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST_CODE:
                if(resultCode == -1) {
                    Bitmap bitmap = decodeUri(data.getData());
                    ivProfile.setImageBitmap(bitmap);
                    groupToUpdate.setPic(bitmap);
                }
                break;
            case CONTACT_PICKER_REQUEST_CODE:
                if(resultCode == -1) {
                    vMembers.add(new Contact(String.valueOf(data.getExtras().getString("contact_name")),
                                             data.getExtras().getInt("contact_id")));
                    contactAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
                }
                break;
            case NAME_PICKER_REQUEST_CODE:
                if(resultCode == -1)
                    tvGroupName.setText(String.valueOf(data.getExtras().getString("name")));
                break;
            case DESCRIPTION_PICKER_REQUEST_CODE:
                if(resultCode == -1)
                    tvGroupDescription.setText(String.valueOf(data.getExtras().getString("description")));
                break;
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.CREATE_GROUP_OK:
                if(save_mode == OVERWRITE_MODE) {
                    communication.insertMembers(vMembers, groupToUpdate.getId());
                }else {
                    communication.insertMembers(vMembers, (Integer) tupla.b);
                }

                break;
            case GroupCommunication.INSERT_MEMBERS_OK:
                Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();
                finish();
                break;
            case GroupCommunication.GET_MEMBERS_OK:
                vMembers.clear();
                if(!activityMode.equalsIgnoreCase(VIEW_MODE)) vMembers.add(new Contact("-1"));
                if(activityMode.equalsIgnoreCase(CREATE_MODE)) vMembers.add(new Contact(db.getCurrentUsername(), db.getCurrentID()));
                vMembers.addAll((List <Contact>)tupla.b);
                contactAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
                break;
            case GroupCommunication.LEAVE_GROUP_OK:
            case GroupCommunication.DELETE_GROUP_OK:
                finish();
                break;
            case GroupCommunication.DELETE_GROUP_ERROR:
            case GroupCommunication.LEAVE_GROUP_ERROR:
            case GroupCommunication.CREATE_GROUP_ERROR:
            case GroupCommunication.GET_MEMBERS_ERROR:
            case GroupCommunication.INSERT_MEMBERS_ERROR:
                String errorMsg = (String) tupla.b;
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Transforma una uri en bitmap
     * @param uri .
     * @return
     */
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(contactAdapter.getContactName(i).equalsIgnoreCase("-1")){
            Intent intent = new Intent(this,
                    AddContactActivity.class);
            intent.putExtra("S","");
            startActivityForResult(intent, CONTACT_PICKER_REQUEST_CODE);
        }
    }
}