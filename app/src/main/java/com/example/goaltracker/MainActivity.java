package com.example.goaltracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoalAdapter.GoalAdapterListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
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

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.goal_name_field);
        EditText descField = dialogView.findViewById(R.id.goal_desc_field);
        Button saveButton = dialogView.findViewById(R.id.save_goal_button);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Már itt vagyunk
        } else if (id == R.id.nav_stats) {
            Intent intent = new Intent(this, StatsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}