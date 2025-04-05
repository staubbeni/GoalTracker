package com.example.goaltracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button registerButton;
    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        registerButton = findViewById(R.id.register_button);
        db = new DatabaseHelper(this);

        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            if (db.insertUser(email, password)) {
                Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                int userId = db.checkUser(email, password);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("userId", userId);
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Hiba történt!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}