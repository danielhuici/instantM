package info.androidhive.loginandregistration.session;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.chats.ChatsActivity;
import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

public class LoginActivity extends Activity  implements Observer {
    private Button btnLogin;
    private TextView txtLinkToRegister;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private LoginCommunication communication;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.login_username);
        inputPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.button_login);
        txtLinkToRegister = findViewById(R.id.lbRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        communication = new LoginCommunication();
        communication.addObserver(this);


        // Comprobar si el usuuario está dentro...
        if (session.isLoggedIn()) {
            // Lo llevamos a la Main Activity!!!
            Intent intent = new Intent(LoginActivity.this, ChatsActivity.class);
            startActivity(intent);
            finish();
        }

        // Botón login
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Comprobar que los datos no están vacíos
                if (!email.isEmpty() && !password.isEmpty()) {
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Por favor, introduce los datos", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Botón link a registro
        txtLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Comprobar datos conrrectos con MySQL
     * */
    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request

        pDialog.setMessage("Entrando ...");
        communication.login(username, password);
    }




    @Override
    public void update(Observable observable, Object o) {
        Tupla<String, Object> tupla = (Tupla<String, Object>) o;
        switch (tupla.a){
            case LoginCommunication.OK:
                session.setLogin(true);
                User user = (User) tupla.b;

                // Inserting row in users table
                db.addUser(user);

                // Lanzar Main Activity
                Intent intent = new Intent(this,
                        ChatsActivity.class);
                startActivity(intent);
                finish();
                break;
            case LoginCommunication.ERROR:
                String errorMsg = (String) tupla.b;
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
