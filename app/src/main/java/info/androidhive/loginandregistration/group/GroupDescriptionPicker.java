package info.androidhive.loginandregistration.group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import info.androidhive.loginandregistration.R;

/**
 * Controla la ventana dedicada a la obtención de la descripción de un grupo.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class GroupDescriptionPicker extends AppCompatActivity implements View.OnClickListener {

    private EditText etDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_description_picker);

        Button btCancelGroupDescription = findViewById(R.id.btCancelGroupDescription);
        Button btOKGroupDescription = findViewById(R.id.btOkGroupDescription);
        etDescription = findViewById(R.id.etGroupDescription);
        btOKGroupDescription.setOnClickListener(this);
        btCancelGroupDescription.setOnClickListener(this);

        if(getIntent().getExtras() != null)
            etDescription.setText(getIntent().getExtras().getString("description"));
    }

    @Override
    public void onClick(View view) {
        Intent output;
        switch (view.getId()){
            case R.id.btCancelGroupDescription:
                returnNoOk();
            case R.id.btOkGroupDescription:
                if(! etDescription.getText().toString().trim().equals("")) {
                    output = new Intent();
                    output.putExtra("description", etDescription.getText().toString());
                    setResult(RESULT_OK, output);
                    finish();
                }else{
                    returnNoOk();
                }
        }
    }

    /**
     * Retorna el valor que indica que se ha cancelado la operación relativa a la activity.
     */
    private void returnNoOk(){
        setResult(RESULT_CANCELED, null);
        finish();
    }

}
