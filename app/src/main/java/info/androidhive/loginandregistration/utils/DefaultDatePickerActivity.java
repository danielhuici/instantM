package info.androidhive.loginandregistration.utils;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.session.DatePickerFragment;

public class DefaultDatePickerActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_date_picker);

        Button btCancel = findViewById(R.id.btCancel);
        Button btOK = findViewById(R.id.btOk);
        etDate = findViewById(R.id.etDate);
        btOK.setOnClickListener(this);
        btCancel.setOnClickListener(this);

        if(getIntent().getExtras() != null) {
            etDate.setHint(getIntent().getExtras().getString("etText"));
            btOK.setText(getIntent().getExtras().getString("okText"));
            btCancel.setText(getIntent().getExtras().getString("cancelText"));
        }
        etDate.setFocusable(false);
        //etDate.setEnabled(false);
        etDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent output;
        switch (view.getId()){
            case R.id.btCancel:
                returnNoOk();
            case R.id.btOk:
                if(! etDate.getText().toString().trim().equals("")) {
                    output = new Intent();
                    output.putExtra("result", etDate.getText().toString());
                    setResult(RESULT_OK, output);
                    finish();
                }else{
                    returnNoOk();
                }
            case R.id.etDate:
                showDatePickerDialog();
                break;
        }
    }
    private void returnNoOk(){
        setResult(RESULT_CANCELED, null);
        finish();
    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "-" + (month+1) + "-" + year;
                etDate.setText(selectedDate);
            }
        });

        newFragment.show(getFragmentManager(), "date");
    }
}
