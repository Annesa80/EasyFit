package com.example.easyfit;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class moodAdapter extends RecyclerView.Adapter<moodAdapter.MoodViewHolder> {

    private final ArrayList<mood> moodList;

    public moodAdapter(ArrayList<mood> moodList) {
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        mood mood = moodList.get(position);
        holder.textViewMood.setText("Mood: " + mood.getMood());
        holder.textViewDate.setText("Date: " + mood.getDate());
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMood, textViewDate;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMood = itemView.findViewById(R.id.textViewMood);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}