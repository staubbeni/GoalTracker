package com.example.goaltracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StatsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private int userId;
    private TextView totalGoalsText;
    private TextView completedGoalsText;
    private TextView incompleteGoalsText;
    private TextView feedbackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getIntExtra("userId", -1);
        db = new DatabaseHelper(this);

        totalGoalsText = findViewById(R.id.total_goals_text);
        completedGoalsText = findViewById(R.id.completed_goals_text);
        incompleteGoalsText = findViewById(R.id.incomplete_goals_text);
        feedbackText = findViewById(R.id.feedback_text);

        int totalGoals = db.getTotalGoalsCount(userId);
        int completedGoals = db.getCompletedGoalsCount(userId);
        int incompleteGoals = totalGoals - completedGoals;

        totalGoalsText.setText("Összes cél: " + totalGoals);
        completedGoalsText.setText("Elért célok: " + completedGoals);
        incompleteGoalsText.setText("Hátralévő célok: " + incompleteGoals);

        if (completedGoals >= 5) {
            feedbackText.setText("Szuper vagy! Már " + completedGoals + " célt elértél!");
        } else if (completedGoals > 0) {
            feedbackText.setText("Jó úton jársz! Már " + completedGoals + " célt elértél!");
        } else {
            feedbackText.setText("Ne add fel! Kezdj egy kis céllal!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}