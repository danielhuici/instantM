package info.androidhive.loginandregistration.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import info.androidhive.loginandregistration.R;

/**
 * Clase genérica que controla la ventana dedicada a la obtención de texto.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class DefaultPickerActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_picker);

        Button btCancel = findViewById(R.id.btCancel);
        Button btOK = findViewById(R.id.btOk);
        etPicker = findViewById(R.id.etPicker);
        btOK.setOnClickListener(this);
        btCancel.setOnClickListener(this);

        if(getIntent().getExtras() != null) {
            etPicker.setText(getIntent().getExtras().getString("etText"));
            btOK.setText(getIntent().getExtras().getString("okText"));
            btCancel.setText(getIntent().getExtras().getString("cancelText"));
        }
    }

    @Override
    public void onClick(View view) {
        Intent output;
        switch (view.getId()){
            case R.id.btCancel:
                returnNoOk();
            case R.id.btOk:
                if(! etPicker.getText().toString().trim().equals("")) {
                    output = new Intent();
                    output.putExtra("result", etPicker.getText().toString());
                    setResult(RESULT_OK, output);
                    finish();
                }else{
                    returnNoOk();
                }
        }
    }
    private void returnNoOk(){
        setResult(RESULT_CANCELED, null);
        finish();
    }
}
