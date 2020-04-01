package com.example.ekisan.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ekisan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView userPhoto;
static int pREQCODE=1;
static int requestCode=1;
Uri imagepicked;
private EditText userEmail,userPassword,userPassword2,userName;
private ProgressBar lodingProgress;
private Button regButton;
private FirebaseAuth mAuth;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==1 && data !=null)
        {
imagepicked=data.getData();
userPhoto.setImageURI(imagepicked);




        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //image views
        userEmail=(EditText)findViewById(R.id.regemail);
        userName=(EditText)findViewById(R.id.name);
        userPassword=(EditText)findViewById(R.id.regpassword);
        userPassword2=(EditText)findViewById(R.id.regpassenter);
        lodingProgress=(ProgressBar)findViewById(R.id.progressBar);
        regButton=(Button)findViewById(R.id.regbtn);
        lodingProgress.setVisibility(View.INVISIBLE);


        mAuth= FirebaseAuth.getInstance();
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                regButton.setVisibility(View.INVISIBLE);
                lodingProgress.setVisibility(View.VISIBLE);
                final String email=userEmail.getText().toString();
                final String password=userPassword.getText().toString();
                final String password2=userPassword2.getText().toString();
                final String name1=userName.getText().toString();
                if(imagepicked==null)
                {
                    showMessage("Please select a User image");
                    regButton.setVisibility(View.VISIBLE);
                    lodingProgress.setVisibility(View.INVISIBLE);

                }
                if(email.isEmpty()||name1.isEmpty()||password.isEmpty()||!password2.equals(password)&&imagepicked!=null)
                {

                    //Validation Errors and User has Entered incorrect information
                    showMessage("Please Enter all the fields");
                    regButton.setVisibility(View.VISIBLE);
                    lodingProgress.setVisibility(View.INVISIBLE);


                }
                else
                {
//everything is ok and all fields are filled
                    if(imagepicked!=null)
                    CreateUserAcccount(email,name1,password);



                }



            }
        });

        userPhoto= findViewById(R.id.regUser);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=22)
                {

                    checkAndRequestForPermission();


                }
                else
                {
                    openGallery();

                }



            }
        });
    }

    private void CreateUserAcccount(String email,  String name2, String password) {
       final String name=name2;   // this method create user account with specific email and password
mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
if(task.isSuccessful())
{

    showMessage("Account Created Successfully");
    //after the account is created we need to update his profile and name
    if(imagepicked!=null)

        updateUserInfo(name,imagepicked,mAuth.getCurrentUser());
    else


    updateUserInfowithoutPhoto(name,mAuth.getCurrentUser());


}
else
{

   //account creation failed
   showMessage("Account Not Created");
   regButton.setVisibility(View.VISIBLE);
   lodingProgress.setVisibility(View.INVISIBLE);


}
    }
});


    }
//update user photo and name
private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

    // first we need to upload user photo to firebase storage and get url

    StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
    final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            // image uploaded succesfully
            // now we can get our image url

            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    // uri contain user image url


                    UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(uri)
                            .build();


                    currentUser.updateProfile(profleUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        // user info updated successfully
                                        showMessage("Register Complete");
                                        updateUI();
                                    }

                                }
                            });

                }
            });





        }
    });






}

    private void updateUserInfowithoutPhoto(final String name, final FirebaseUser currentUser) {

        // first we need to upload user photo to firebase storage and get url



                        // uri contain user image url


                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)

                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            showMessage("Register Complete");
                                            updateUI();
                                        }

                                    }
                                });

                    }












    private void updateUI() {
Intent homeActivity=new Intent(getApplicationContext(),Home1.class);
startActivity(homeActivity);
finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        //open the gallery and wait for the user to pick the photo
 Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
 galleryIntent.setType("image/*");
 startActivityForResult(galleryIntent,requestCode);




    }

    private void checkAndRequestForPermission() {

if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
{
if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
{

    Toast.makeText(RegisterActivity.this,"Please give the required permission",Toast.LENGTH_LONG).show();


}
else
{
    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, pREQCODE);


}


}
else
{
    openGallery();



}
    }


}
