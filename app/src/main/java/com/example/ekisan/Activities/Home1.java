package com.example.ekisan.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.ekisan.Fragments.HomeFragment;
import com.example.ekisan.Fragments.ProfileFragment;
import com.example.ekisan.Fragments.SettingFragment;
import com.example.ekisan.Models.Post;
import com.example.ekisan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.ekisan.Activities.RegisterActivity.pREQCODE;
import static com.example.ekisan.Activities.RegisterActivity.requestCode;

public class Home1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    Dialog popaddpost;
    ImageView popupuserImage,popuppostimage,popupadd;
    TextView popupTitle,popupdescrp;
    ProgressBar popupClickpbar;
    private static final int pREQCODE=2;
    private static final int requestCode=1;
    private Uri imagepicked=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ekisan");

        setContentView(R.layout.activity_home4);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        iniPopup();
        setupPopupImaeClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              popaddpost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    private void setupPopupImaeClick() {
        popuppostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here when we click we need to open the gallery
                //Register activity
checkAndRequestForPermission();
            }
        });



    }

    private void iniPopup() {
        popaddpost=new Dialog(this);
popaddpost.setContentView(R.layout.pop_up_post);
popaddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
popaddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
popaddpost.getWindow().getAttributes().gravity=Gravity.TOP;
popupuserImage=popaddpost.findViewById(R.id.pop_upusermage);
popuppostimage=popaddpost.findViewById(R.id.pop_up_img);
popupadd=popaddpost.findViewById(R.id.pop_up_create);
popupTitle=popaddpost.findViewById(R.id.popuptitlr);
popupdescrp=popaddpost.findViewById(R.id.popupdesc);
popupClickpbar=popaddpost.findViewById(R.id.pop_up_progress);
Glide.with(Home1.this).load(currentuser.getPhotoUrl()).into(popupuserImage);

popupadd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        popupClickpbar.setVisibility(View.VISIBLE);
        popupadd.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(!popupTitle.getText().toString().isEmpty()&&!popupdescrp.getText().toString().isEmpty()&&imagepicked!=null)
            {

                //Create the Post Object and add the post to the firebase
                //
                // Post post=new Post()
                //first we need to upload post image
                //access firebase storage
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("blog_images");
                final StorageReference imageFilePath=storageReference.child(imagepicked.getLastPathSegment())
    ;
                imageFilePath.putFile(imagepicked).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                           String imageDownloadLink=uri.toString();
                           //create post objec

                            Post post=new Post(popupTitle.getText().toString(),popupdescrp.getText().toString(),imageDownloadLink,currentuser.getUid(),currentuser.getPhotoUrl().toString(),currentuser.getDisplayName());
                            //Add post to the firebase database
                               addPost(post);
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               showMesage(e.getMessage());
                               popupClickpbar.setVisibility(View.INVISIBLE);
                               popupadd.setVisibility(View.VISIBLE);

                           }
                       }) ;
                    }
                });



            }
            else
            {

                showMesage("Invalid Credentials!! verify all the input fields");
                popupClickpbar.setVisibility(View.INVISIBLE);
                popupadd.setVisibility(View.VISIBLE);


            }
        }
    }
});


    }

    private void addPost(Post post) {
        FirebaseDatabase database= FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("Posts").push();
        String key=myRef.getKey();
        post.setPostKey(key);
        //add post data to the firebase databse
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMesage("POst Added Successfully");
                popupClickpbar.setVisibility(View.INVISIBLE);
                popupadd.setVisibility(View.VISIBLE);
                popaddpost.dismiss();

            }
        });

    }

    private void showMesage(String invalid_) {
        Toast.makeText(Home1.this,invalid_,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            getSupportActionBar().setTitle("Community Discussion");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingFragment()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Crop Disease Detection");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();


        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent LoginActivity=new Intent(getApplicationContext(), com.example.ekisan.Activities.LoginActivity.class);
            startActivity(LoginActivity);
            finish();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader()
    {

        NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        TextView navUsername=headerView.findViewById(R.id.nav_userName);
        TextView navUserMail=headerView.findViewById(R.id.nav_userEmail);
        ImageView navUserPhoto=headerView.findViewById(R.id.nav_userPhoto);
        navUserMail.setText(currentuser.getEmail());
        navUsername.setText(currentuser.getDisplayName());

        Glide.with(this).load(currentuser.getPhotoUrl()).into(navUserPhoto);





    }
    private void checkAndRequestForPermission() {

        if(ContextCompat.checkSelfPermission(Home1.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Home1.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {

                Toast.makeText(Home1.this,"Please give the required permission",Toast.LENGTH_LONG).show();


            }
            else
            {
                ActivityCompat.requestPermissions(Home1.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, pREQCODE);


            }


        }
        else
        {
            openGallery();



        }
    }



    private void openGallery() {
        //open the gallery and wait for the user to pick the photo
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==1 && data !=null)
        {
            imagepicked=data.getData();
            popuppostimage.setImageURI(imagepicked);






        }
    }

}
