package com.example.goaltracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {
    private TextView statsText;
    private DatabaseHelper db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsText = findViewById(R.id.stats_text);
        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        int completed = db.getCompletedGoalsCount(userId);
        int total = db.getTotalGoalsCount(userId);
        statsText.setText("Teljesített célok: " + completed + "\nÖsszes cél: " + total);
    }
}