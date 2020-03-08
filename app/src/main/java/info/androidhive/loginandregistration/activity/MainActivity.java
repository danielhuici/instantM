package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;

	private SQLiteHandler db;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		btnLogout = (Button) findViewById(R.id.button_logout);

		// session manager
        session = new SessionManager(getApplicationContext());
		db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            //logoutUser();
        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

	}
	/*

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnLogout = (Button) findViewById(R.id.button_logout);

		// session manager
		session = new SessionManager(getApplicationContext());

		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());


		if (!session.isLoggedIn()) {
			logoutUser();
		}

		/*
		txtName = (TextView) findViewById(R.id.login_username);
		txtEmail = (TextView) findViewById(R.id.register_email);
		//





		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEmail.setText(email);
*/

		// Logout button click event
		/*btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});


	}*/


}
