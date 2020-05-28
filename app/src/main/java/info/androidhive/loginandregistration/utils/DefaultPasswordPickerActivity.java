package info.androidhive.loginandregistration.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import info.androidhive.loginandregistration.R;
/**
 * Clase genérica que controla la ventana dedicada a la obtención de contraseñas.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class DefaultPasswordPickerActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {
    private Button btOK;
    private EditText etTypePassword;
    private EditText etRetypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_password_picker);
        Button btCancel = findViewById(R.id.btCancel);
        btOK = findViewById(R.id.btOk);
        etTypePassword = findViewById(R.id.etNewPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etTypePassword.addTextChangedListener(this);
        etRetypePassword.addTextChangedListener(this);
        btOK.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btOK.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        Intent output;
        switch (view.getId()){
            case R.id.btCancel:
                returnNoOk();
            case R.id.btOk:
                output = new Intent();
                output.putExtra("result", etTypePassword.getText().toString());
                setResult(RESULT_OK, output);
                finish();

        }
    }
    private void returnNoOk(){
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!etTypePassword.getText().toString().equals("") &&
                etTypePassword.getText().toString().equals(etRetypePassword.getText().toString())) {
             btOK.setEnabled(true);
        } else{
            btOK.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}
