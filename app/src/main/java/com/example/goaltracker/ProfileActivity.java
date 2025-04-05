package com.example.goaltracker;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private int userId;
    private EditText nicknameField;
    private EditText birthDateField;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getIntExtra("userId", -1);
        db = new DatabaseHelper(this);

        nicknameField = findViewById(R.id.nickname_field);
        birthDateField = findViewById(R.id.birth_date_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        Button saveButton = findViewById(R.id.save_button);

        loadUserData();

        saveButton.setOnClickListener(v -> {
            String nickname = nicknameField.getText().toString();
            String birthDate = birthDateField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email és jelszó kötelező!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateUser(userId, email, password, nickname, birthDate)) {
                Toast.makeText(this, "Adatok mentve!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiba történt!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        Cursor cursor = db.getUser(userId);
        if (cursor.moveToFirst()) {
            emailField.setText(cursor.getString(0));
            passwordField.setText(cursor.getString(1));
            String nickname = cursor.getString(2);
            String birthDate = cursor.getString(3);
            if (nickname != null) {
                nicknameField.setText(nickname);
            }
            if (birthDate != null) {
                birthDateField.setText(birthDate);
            }
        }
        cursor.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}