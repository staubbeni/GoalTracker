package com.example.goaltracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalAdapterListener {
    private RecyclerView goalsRecyclerView;
    private Button addGoalButton, statsButton;
    private DatabaseHelper db;
    private ArrayList<Goal> goalsList;
    private GoalAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        goalsRecyclerView = findViewById(R.id.goals_recycler_view);
        addGoalButton = findViewById(R.id.add_goal_button);
        statsButton = findViewById(R.id.stats_button);
        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);
        goalsList = new ArrayList<>();

        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GoalAdapter(goalsList, db, this);
        goalsRecyclerView.setAdapter(adapter);

        loadGoals();

        addGoalButton.setOnClickListener(v -> showAddGoalDialog());
        statsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void loadGoals() {
        goalsList.clear();
        Cursor cursor = db.getGoals(userId);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(2);
            String desc = cursor.getString(3);
            boolean completed = cursor.getInt(4) == 1;
            goalsList.add(new Goal(id, name, desc, completed));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.goal_name_field);
        EditText descField = dialogView.findViewById(R.id.goal_desc_field);
        Button saveButton = dialogView.findViewById(R.id.save_goal_button);

        AlertDialog dialog = builder.create();
        saveButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String desc = descField.getText().toString();
            if (!name.isEmpty()) {
                db.insertGoal(userId, name, desc);
                loadGoals();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onGoalDeleted() {
        loadGoals();
    }
}