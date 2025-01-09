package com.example.easyfit.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfit.CommentFragment;
import com.example.easyfit.MainActivity;
import com.example.easyfit.R;
import com.example.easyfit.models.ModelPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        //get data
        ModelPost modelPost = postList.get(i);
        String uid = modelPost.getuid();
        String uEmail = modelPost.getuEmail();
        String uName = modelPost.getuName();
        String uDp = modelPost.getuDp();
        String pId = modelPost.getpId();
        String pDescr = modelPost.getpDescr();
        String pImage = modelPost.getpImage();

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid) // Use uid to locate the user in the database
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //fetch username
                            String fetchedUsername = snapshot.child("username").getValue(String.class);
                            holder.TVUsername.setText(fetchedUsername); // Set the username

                            //fetch profile pic
                            String fetchedProfilePic = snapshot.child("profileImageURL").getValue(String.class);
                            if (fetchedProfilePic != null && !fetchedProfilePic.isEmpty()) {
                                Picasso.get().load(fetchedProfilePic)
                                        .placeholder(R.drawable.ic_outline_person_24)
                                        .into(holder.IVProfile);
                            } else {
                                holder.IVProfile.setImageResource(R.drawable.ic_outline_person_24); // Default image
                            }

                        } else {
                            holder.TVUsername.setText("Unknown User"); // Default if user not found
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error fetching username", Toast.LENGTH_SHORT).show();
                    }
                });

        //set data
        holder.TVUsername.setText(uName);
        holder.TVDescription.setText(pDescr);

//        case without description
//        if (pDescr.trim().isEmpty()) {
//            holder.TVDescription.setVisibility(View.GONE);
//        } else {
//            holder.TVDescription.setText(pDescr);
//            holder.TVDescription.setVisibility(View.VISIBLE);
//        }

        //set user dp
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_outline_person_24).into(holder.IVProfile);
        } catch (Exception e) {
            holder.IVProfile.setImageResource(R.drawable.ic_outline_person_24);
        }

        //set post image
        // if there is no image i.e. pImage.equals("noImage") then hide IMageView
        if (pImage.equals("noImage")) {
            holder.IVPost.setVisibility(View.GONE);
        } else {
            try {
                Picasso.get().load(pImage).into(holder.IVPost);
            } catch (Exception e) {

            }
        }

        //handle button clicks
        holder.IBComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentFragment commentFragment = new CommentFragment();

                // Pass postId to the fragment
                Bundle args = new Bundle();
                args.putString("pId", modelPost.getpId()); // Assuming `modelPost` has a `getPId()` method
                commentFragment.setArguments(args);

                // Navigate to the fragment
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, commentFragment) // Replace with your container ID
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //view from row_post.xml
        ImageView IVProfile, IVPost;
        TextView TVUsername, TVDescription;
        ImageButton IBComment;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            IVProfile = itemView.findViewById(R.id.IVProfile);
            IVPost = itemView.findViewById(R.id.IVPost);
            TVUsername = itemView.findViewById(R.id.TVUsername);
            TVDescription = itemView.findViewById(R.id.TVDescription);
            IBComment = itemView.findViewById(R.id.IBComment);

        }
    }
}
