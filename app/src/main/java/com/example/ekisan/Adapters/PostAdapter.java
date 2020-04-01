package com.example.ekisan.Adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ekisan.Activities.PostDetails;
import com.example.ekisan.Models.Post;
import com.example.ekisan.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {


    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);

        return new MyViewHolder(row);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgpostprofile);

    }


    @Override
    public int getItemCount() {
       return mData.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView tvTitle;
        ImageView imgPost;
        ImageView imgpostprofile;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.row_post_title);
            imgPost=itemView.findViewById(R.id.row_post_image1);
            imgpostprofile=itemView.findViewById(R.id.row_post_prfile_imh);
itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent postDetailActivity=new Intent(mContext, PostDetails.class);
        int position=getAdapterPosition();

        postDetailActivity.putExtra("title",mData.get(position).getTitle());
        postDetailActivity.putExtra("postImage1",mData.get(position).getPicture());
        postDetailActivity.putExtra("description",mData.get(position).getDescription());
        postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
        postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
        if(mData.get(position).getUserName()==null)
            postDetailActivity.putExtra("post_user","No Name");
        else
            postDetailActivity.putExtra("post_user",mData.get(position).getUserName());



        mContext.startActivity(postDetailActivity);



    }
});

        }
    }

}
