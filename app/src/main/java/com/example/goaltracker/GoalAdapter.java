package com.example.goaltracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private ArrayList<Goal> goalsList;
    private DatabaseHelper db;
    private GoalAdapterListener listener;

    public interface GoalAdapterListener {
        void onGoalDeleted();
    }

    public GoalAdapter(ArrayList<Goal> goalsList, DatabaseHelper db, GoalAdapterListener listener) {
        this.goalsList = goalsList;
        this.db = db;
        this.listener = listener;
    }

    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoalViewHolder holder, int position) {
        Goal goal = goalsList.get(position);
        holder.goalName.setText(goal.getName());
        holder.goalCheckbox.setChecked(goal.isCompleted());

        holder.goalCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            goal.setCompleted(isChecked);
            db.updateGoal(goal.getId(), isChecked);
        });

        holder.deleteButton.setOnClickListener(v -> {
            db.deleteGoal(goal.getId());
            goalsList.remove(position);
            notifyItemRemoved(position);
            listener.onGoalDeleted();
        });
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        CheckBox goalCheckbox;
        TextView goalName;
        Button deleteButton;

        public GoalViewHolder(View itemView) {
            super(itemView);
            goalCheckbox = itemView.findViewById(R.id.goal_checkbox);
            goalName = itemView.findViewById(R.id.goal_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}