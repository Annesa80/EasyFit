package com.example.easyfit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfit.R;
import com.example.easyfit.models.ModelComment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder>{

    Context context;
    List<ModelComment> commentList;

    public AdapterComments(Context context, List<ModelComment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //bind the row-comment.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        //get the data
        String uid = commentList.get(i).getUid();
        String uEmail = commentList.get(i).getuEmail();
        String uDp = commentList.get(i).getuDp();
        String uName = commentList.get(i).getuName();
        String cId = commentList.get(i).getcId();
        String comment = commentList.get(i).getComment();

        //set the data
        holder.TVUsername.setText(uName);
        holder.TVComment.setText(comment);

        // Fetch username and profile picture from Firebase using UID
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid) // Use uid to locate the user in the database
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Fetch username
                            String fetchedUsername = snapshot.child("username").getValue(String.class);
                            holder.TVUsername.setText(fetchedUsername != null ? fetchedUsername : "Unknown User");

                            // Fetch profile picture
                            String fetchedProfilePic = snapshot.child("profileImageURL").getValue(String.class);
                            if (fetchedProfilePic != null && !fetchedProfilePic.isEmpty()) {
                                Picasso.get().load(fetchedProfilePic)
                                        .placeholder(R.drawable.ic_outline_person_24)
                                        .into(holder.IVProfile);
                            } else {
                                holder.IVProfile.setImageResource(R.drawable.ic_outline_person_24); // Default image
                            }
                        } else {
                            holder.TVUsername.setText("Unknown User");
                            holder.IVProfile.setImageResource(R.drawable.ic_outline_person_24); // Default profile picture
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error while fetching user data
                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        //declare view from row_comments.cml
        ImageView IVProfile;
        TextView TVUsername, TVComment;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            IVProfile = itemView.findViewById(R.id.IVProfile);
            TVUsername = itemView.findViewById(R.id.TVUsername);
            TVComment = itemView.findViewById(R.id.TVComment);
        }
    }
}

