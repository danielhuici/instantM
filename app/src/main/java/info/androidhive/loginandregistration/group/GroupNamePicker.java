package info.androidhive.loginandregistration.group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.androidhive.loginandregistration.R;

public class GroupNamePicker extends AppCompatActivity implements View.OnClickListener {

    private Button btOKGroupName;
    private Button btCancelGroupName;
    private EditText etName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_name);

        btCancelGroupName = findViewById(R.id.btCancelGroupName);
        btOKGroupName = findViewById(R.id.btOkGroupName);
        etName = findViewById(R.id.etGroupName);
        btOKGroupName.setOnClickListener(this);
        btCancelGroupName.setOnClickListener(this);

        if(getIntent().getExtras() != null)
                etName.setText(getIntent().getExtras().getString("name"));
    }

    @Override
    public void onClick(View view) {
        Intent output;
        switch (view.getId()){
            case R.id.btCancelGroupName:
              returnNoOk();
            case R.id.btOkGroupName:
                if(! etName.getText().toString().trim().equals("")) {
                    output = new Intent();
                    output.putExtra("name", etName.getText().toString());
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
