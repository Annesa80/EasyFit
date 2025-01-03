package com.example.easyfit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HealthTipAdapter extends RecyclerView.Adapter<HealthTipAdapter.HealthTipViewHolder> {

    private final List<String> healthTips;

    public HealthTipAdapter(List<String> healthTips) {
        this.healthTips = healthTips;
    }

    @NonNull
    @Override
    public HealthTipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_health_tip, parent, false);
        return new HealthTipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthTipViewHolder holder, int position) {
        String tip = healthTips.get(position);
        holder.textHealthTip.setText(tip);

        // Handle Like Button Click
        holder.likeButton.setOnClickListener(v -> {
            boolean isLiked = (boolean) holder.likeButton.getTag();
            if (isLiked) {
                holder.likeButton.setImageResource(R.drawable.ic_like);
                Toast.makeText(v.getContext(), "Unliked", Toast.LENGTH_SHORT).show();
            } else {
                holder.likeButton.setImageResource(R.drawable.ic_liked);
                Toast.makeText(v.getContext(), "Liked", Toast.LENGTH_SHORT).show();
            }
            holder.likeButton.setTag(!isLiked);
        });

        // Handle Share Button Click
        holder.shareButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, tip);
            context.startActivity(Intent.createChooser(shareIntent, "Share Health Tip"));
        });
    }

    @Override
    public int getItemCount() {
        return healthTips.size();
    }

    static class HealthTipViewHolder extends RecyclerView.ViewHolder {
        TextView textHealthTip;
        ImageButton likeButton, shareButton;

        public HealthTipViewHolder(@NonNull View itemView) {
            super(itemView);
            textHealthTip = itemView.findViewById(R.id.text_health_tip);
            likeButton = itemView.findViewById(R.id.like_button);
            shareButton = itemView.findViewById(R.id.share_button);
            likeButton.setTag(false); // Initial state: not liked
        }
    }
}
