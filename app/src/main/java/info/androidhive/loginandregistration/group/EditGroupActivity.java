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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.AddContactActivity;
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
    private Button btInsertContact;

    private SQLiteHandler db;

    private ArrayList<Contact> vMembers;
    private ContactAdapter contactAdapter;
    private ListView lvMembers;
    private ImageView ivProfile;
    private ProgressDialog pDialog;
    private GroupCommunication communication;
    private Bundle extra;
    private Group groupToUpdate;
    private String SAVE_MODE = "OVERWRITE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        etGroupName = findViewById(R.id.group_name);
        etGroupDescription = findViewById(R.id.group_description);
        btnEditGroup = findViewById(R.id.btEditGroup);
        btnOutGroup = findViewById(R.id.btLeaveGroup);
        btnDeleteGroup = findViewById(R.id.btDeleteGroup);
        btInsertContact = findViewById(R.id.btAddMemberEdit);

        ivProfile = findViewById(R.id.ivGroupImage);
        ivProfile.setOnClickListener(this);
        loadGroup();

        btnEditGroup.setOnClickListener(this);
        btnOutGroup.setOnClickListener(this);
        btnDeleteGroup.setOnClickListener(this);
        btInsertContact.setOnClickListener(this);

        db = new SQLiteHandler(getApplicationContext());
        communication = new GroupCommunication();
        communication.addObserver(this);

        vMembers = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, vMembers);
        lvMembers = findViewById(R.id.listMembers);
        lvMembers.setAdapter(contactAdapter);

        contactAdapter.notifyDataSetChanged();

        communication.getMembers(groupToUpdate.getId());
        setListViewHeightBasedOnChildren(lvMembers, contactAdapter);
    }

    private void loadGroup() {
        extra=getIntent().getExtras();

        if(extra != null) {
                groupToUpdate = (Group) extra.getSerializable("group");
                etGroupName.setText(String.valueOf(groupToUpdate.getName()));
                if(!groupToUpdate.getDescription().equalsIgnoreCase("null") &&
                        groupToUpdate.getDescription() != null) {
                    etGroupDescription.setText(groupToUpdate.getDescription());
                }
                etGroupName.setText(String.valueOf(groupToUpdate.getName()));
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
                startActivityForResult(intent, 1);
                break;
            case R.id.btEditGroup:
                if (!etGroupName.getText().toString().trim().isEmpty()) {
                    groupToUpdate.setName(String.valueOf(etGroupName.getText()));
                    groupToUpdate.setDescription(String.valueOf(etGroupDescription.getText()));
                    communication.crateGroup(groupToUpdate, db.getCurrentUsername(), SAVE_MODE);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btAddMemberEdit:
                Intent intentContact = new Intent(this,
                        AddContactActivity.class);
                intentContact.putExtra("contact", new String());
                startActivityForResult(intentContact, 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch(requestCode) {
            case 1:
                Bitmap bitmap = decodeUri(data.getData());
                ivProfile.setImageBitmap(bitmap);
                groupToUpdate.setPic(bitmap);
                break;
            case 2:
                vMembers.add(new Contact(String.valueOf(data.getExtras().getString("contact"))));
                contactAdapter.notifyDataSetChanged();
                break;
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case GroupCommunication.CREATE_GROUP_OK:
                final List<Contact> membersToInsert = vMembers;
                communication.insertMembers(vMembers, groupToUpdate.getId());
                break;
            case GroupCommunication.INSERT_MEMBERS_OK:
                Toast.makeText(getApplicationContext(), "¡Grupo creado exitosamente!", Toast.LENGTH_LONG).show();
                finish();
                break;
            case GroupCommunication.GET_MEMBERS_OK:
                vMembers.clear();
                vMembers.addAll((List <Contact>)tupla.b);
                contactAdapter.notifyDataSetChanged();
                break;
            case GroupCommunication.CREATE_GROUP_ERROR:
            case GroupCommunication.GET_MEMBERS_ERROR:
            case GroupCommunication.INSERT_MEMBERS_ERROR:
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