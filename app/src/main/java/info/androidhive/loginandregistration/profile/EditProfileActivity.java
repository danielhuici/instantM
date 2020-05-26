package info.androidhive.loginandregistration.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.session.User;
import info.androidhive.loginandregistration.utils.DefaultDatePickerActivity;
import info.androidhive.loginandregistration.utils.DefaultPasswordPickerActivity;
import info.androidhive.loginandregistration.utils.DefaultPickerActivity;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

import static info.androidhive.loginandregistration.profile.ProfileCommunication.UPDATE_ERROR;
import static info.androidhive.loginandregistration.profile.ProfileCommunication.UPDATE_OK;

public class EditProfileActivity extends AppCompatActivity implements Observer, View.OnClickListener, AdapterView.OnItemClickListener{

    private Button btEditProfile;


    private SQLiteHandler db;
    private ArrayList<ProfileOption> vOptions;
    private OptionAdapter optionAdapter;
    private ListView lvOptions;
    private ImageView ivProfile;
    private ImageView ivEditProfileState;

    private TextView tvProfileName;
    private TextView tvProfileState;

    private ProfileCommunication communication;

    private User profileToUpdate;


    private final int IMAGE_PICKER_REQUEST_CODE = 0;
    private final int EMAIL_PICKER_REQUEST_CODE = 1;
    private final int STATE_PICKER_REQUEST_CODE = 2;
    private final int PASSWORD_PICKER_REQUEST_CODE = 3;
    private final int DATE_PICKER_REQUEST_CODE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getApplicationContext());
        profileToUpdate = db.getUserDetails();
        setContentView(R.layout.activity_edit_profile);

        initComponents();

        communication = new ProfileCommunication();
        communication.addObserver(this);


        populateFields(profileToUpdate);
        addListeners();

    }

    private void populateFields(User u) {
        tvProfileName.setText(u.getUsername());
        tvProfileState.setText(u.getState());
        vOptions.clear();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit32);
        vOptions.add(new ProfileOption(getString(R.string.menu_item_cambiar_contrasena), icon, ""));
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        vOptions.add(new ProfileOption(getString(R.string.menu_item_cambiar_email), icon, profileToUpdate.getEmail()));
        icon = BitmapFactory.decodeResource(getResources(), R.drawable.birthday64);
        vOptions.add(new ProfileOption(getString(R.string.menu_item_cambiar_fecha_nacimiento), icon, profileToUpdate.getBirthdateString()));
        optionAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(lvOptions, optionAdapter);
    }

    private void addListeners() {
        btEditProfile.setOnClickListener(this);
        tvProfileName.setOnClickListener(this);
        tvProfileState.setOnClickListener(this);

        ivEditProfileState.setOnClickListener(this);
        lvOptions.setOnItemClickListener(this);
    }

    private void initComponents() {
        btEditProfile = findViewById(R.id.btEditProfile);
        btEditProfile.setVisibility(View.INVISIBLE);
        ivProfile = findViewById(R.id.ivProfileImage);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileState = findViewById(R.id.tvProfileDescription);

        ivEditProfileState = findViewById(R.id.ivEditProfileState);

        vOptions = new ArrayList<>();
        optionAdapter = new OptionAdapter(this, vOptions);
        lvOptions = findViewById(R.id.lvProfileOptions);
        lvOptions.setAdapter(optionAdapter);

    }


    public static void setListViewHeightBasedOnChildren(ListView listView, OptionAdapter optionAdapter) {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < optionAdapter.getCount(); i++) {
            view = optionAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (optionAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ivProfileImage:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
                break;
            case R.id.btEditProfile:
                vOptions.remove(0);
                profileToUpdate.setState(tvProfileState.getText().toString());
                communication.updateProfile(profileToUpdate);
                break;
            case R.id.ivEditProfileState:
                Intent picker = new   Intent(this, DefaultPickerActivity.class);
                picker.putExtra("etText", "Introduce el nuevo estado");
                picker.putExtra("okText", "OK");
                picker.putExtra("cancelText", "Cancel");
                startActivityForResult(picker, STATE_PICKER_REQUEST_CODE);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST_CODE:
                if(resultCode == -1) {
                    Bitmap bitmap = decodeUri(data.getData());
                    ivProfile.setImageBitmap(bitmap);
                    profileToUpdate.setPic(bitmap);
                }
                break;
            case STATE_PICKER_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    profileToUpdate.setState(String.valueOf(data.getExtras().getString("result")));
                    tvProfileState.setText(profileToUpdate.getState());
                    populateFields(profileToUpdate);
                }
                break;
            case EMAIL_PICKER_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    profileToUpdate.setEmail(String.valueOf(data.getExtras().getString("result")));
                    populateFields(profileToUpdate);
                }
                break;
            case PASSWORD_PICKER_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    profileToUpdate.setPassword(String.valueOf(data.getExtras().getString("result")));
                    populateFields(profileToUpdate);
                }
                break;
            case DATE_PICKER_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    try {
                        profileToUpdate.setBirthday(String.valueOf(data.getExtras().getString("result")));
                        populateFields(profileToUpdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                break;
        }
        if(resultCode == RESULT_OK){
            btEditProfile.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case UPDATE_OK:
                db.updateUser(profileToUpdate);
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                break;
            case UPDATE_ERROR:
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent picker;

        if (vOptions.get(i).getTitle().equalsIgnoreCase(getString(R.string.menu_item_cambiar_contrasena))) {
            picker = new Intent(this, DefaultPasswordPickerActivity.class);
            startActivityForResult(picker, PASSWORD_PICKER_REQUEST_CODE);

        }else if(vOptions.get(i).getTitle().equalsIgnoreCase(getString(R.string.menu_item_cambiar_email))) {
            picker = new Intent(this, DefaultPickerActivity.class);
            picker.putExtra("etText", "Introduce el nuevo email");
            picker.putExtra("okText", getString(R.string.OK));
            picker.putExtra("cancelText", getString(R.string.cancel));
            startActivityForResult(picker, EMAIL_PICKER_REQUEST_CODE);

        }else if(vOptions.get(i).getTitle().equalsIgnoreCase(getString(R.string.menu_item_cambiar_fecha_nacimiento))){
            picker = new Intent(this, DefaultDatePickerActivity.class);
            picker.putExtra("etText", getString(R.string.birthday));
            picker.putExtra("okText", getString(R.string.OK));
            picker.putExtra("cancelText", getString(R.string.cancel));
            startActivityForResult(picker, DATE_PICKER_REQUEST_CODE);

        }
    }
}
