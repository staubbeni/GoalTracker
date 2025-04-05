package com.example.goaltracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
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

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.dialog_goal_details, null);
            builder.setView(dialogView);

            TextView nameText = dialogView.findViewById(R.id.goal_name_text);
            TextView descText = dialogView.findViewById(R.id.goal_desc_text);
            Button closeButton = dialogView.findViewById(R.id.close_button);

            nameText.setText(goal.getName());
            descText.setText(goal.getDescription());

            AlertDialog dialog = builder.create();
            closeButton.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
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