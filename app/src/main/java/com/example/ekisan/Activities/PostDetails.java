package com.example.ekisan.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ekisan.Adapters.CommentAdapter;
import com.example.ekisan.Models.Comment;
import com.example.ekisan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetails extends AppCompatActivity {
    ImageView imgPost,imgUserPost,imgCurrentUser;
    TextView  txtPostDesc,txtPostDateName,txtPosttitle;
    EditText editTextcomment;
    Button btAddComment;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    RecyclerView rvcomment;
    CommentAdapter commentAdapter;
    List<Comment> listcomment;
    static String Comment_key="Comments";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        imgPost=findViewById(R.id.post_detail_img);
        rvcomment=findViewById(R.id.rv_cooment);

        imgUserPost=findViewById(R.id.post_wroter);
        imgCurrentUser=findViewById(R.id.post_detail_currentuserimg);
        txtPostDesc=findViewById(R.id.post_details_desc);
        txtPostDateName=findViewById(R.id.post_user);
        txtPosttitle=findViewById(R.id.post_detail_title);
        editTextcomment=findViewById(R.id.post_comments);
        btAddComment=findViewById(R.id.post_comment_add);
        Window w=getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        // Data Binding with views
        // Retrive the post data

        String postImage12=getIntent().getExtras().getString("postImage1");
        Glide.with(this).load(postImage12).into(imgPost);
        String postTitle=getIntent().getExtras().getString("title");
        txtPosttitle.setText(postTitle);
        String userpostImage=getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userpostImage).into(imgUserPost);
        String postDescription=getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        user=firebaseAuth.getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl()).into(imgCurrentUser);
        PostKey=getIntent().getExtras().getString("postKey");
        String user_name=getIntent().getExtras().getString("post_user");
        txtPostDateName.setText(user_name);

        iniRvComment();


        btAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference=firebaseDatabase.getReference("Comments").child(PostKey).push();
                String comment_content=editTextcomment.getText().toString();
                String uid=user.getUid();
                String uname=user.getDisplayName();
                String uimg=user.getPhotoUrl().toString();
                Comment comment=new Comment(comment_content,uid,uimg,uname);
                reference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Comment Added");
                        editTextcomment.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to add comment"+e.getMessage());
                    }
                });


            }
        });














    }

    private void iniRvComment() {
rvcomment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference comment=firebaseDatabase.getReference("Comments").child(PostKey);
        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listcomment=new ArrayList<>();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    Comment comm=snap.getValue(Comment.class);
                    listcomment.add(comm);


                }
                commentAdapter=new CommentAdapter(getApplicationContext(),listcomment);
                rvcomment.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void showMessage(String comment_added) {
        Toast.makeText(this,comment_added,Toast.LENGTH_SHORT).show();
    }

    private String timestamptoString(long time)
    {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
       String date=(df.format("dd/MM/yyyy hh:mm:ss", calendar).toString());
       return date;





    }
}
