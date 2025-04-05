package com.example.goaltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", sharedPreferences.getInt("userId", -1));
            startActivity(intent);
            finish();
            return;
        }

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        db = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            int userId = db.checkUser(email, password);
            if (userId != -1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("userId", userId);
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Hibás email vagy jelszó!", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}