package info.androidhive.loginandregistration.session;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.chats.ChatsActivity;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;
/**
 * Clase controladora de la ventana de registro de usuarios.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity implements Observer {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button buttonRegister;
    private EditText inputUsername;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private EditText birthdayDate;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private RegisterCommunication communication;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.register_username);
        inputEmail = findViewById(R.id.register_email);
        inputPassword = findViewById(R.id.register_password);
        inputRepeatPassword = findViewById(R.id.register_repeat_password);
        birthdayDate = findViewById(R.id.birthdayDate);
        buttonRegister = findViewById(R.id.btnRegister);

        communication = new RegisterCommunication();
        communication.addObserver(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Comprobar si un usuario está ya dentro
        if (session.isLoggedIn()) {
            // Dentro!
            Intent intent = new Intent(RegisterActivity.this,
                    ChatsActivity.class);
            startActivity(intent);
            finish();
        }

        // Botón registrarse
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                User user = new User();
                user.setUsername(inputUsername.getText().toString().trim());

                user.setEmail(inputEmail.getText().toString().trim());
                String password = inputPassword.getText().toString().trim();
                String repeatPassword = inputRepeatPassword.getText().toString().trim();
                try {
                    user.setBirthday(birthdayDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!user.getUsername().isEmpty() && !user.getEmail().isEmpty() && !password.isEmpty()) {
                    if(password.equals(repeatPassword)) {
                        user.setPassword(password);
                        registerUser(user);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "¡Las contrasñas no coinciden!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "¡Introduce los datos!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


        birthdayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });




    }

    /*
     * Función para registrar al usuario en la base de datos MySQL
     */
    private void registerUser(final User user) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrando.. ...");


        communication.register(user);

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                month ++;
                String dayString = String.valueOf(day);
                String monthString = String.valueOf(month);

                if (day < 10 ) dayString = "0" + day;
                if (month < 10 ) monthString = "0" + month;

                final String selectedDate = dayString + "-" + monthString + "-" + year;
                birthdayDate.setText(selectedDate);
            }
        });

        newFragment.show(getFragmentManager(), "date");
    }

    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case RegisterCommunication.OK:
                User user = (User) tupla.b;
                System.out.println("EEEEEEEEERERERE" + user.getBirthdate());
                db.addUser(user);

                Toast.makeText(getApplicationContext(), "¡Usuario registrado exitosamente!", Toast.LENGTH_LONG).show();

                // Launch chat activity
                Intent intent = new Intent(
                        RegisterActivity.this,
                        ChatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case RegisterCommunication.ERROR:
                Toast.makeText(getApplicationContext(),
                        (String) tupla.b, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
