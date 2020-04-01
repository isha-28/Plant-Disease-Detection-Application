package com.example.ekisan.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ekisan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userMail=(EditText)findViewById(R.id.userEmail);
        userPassword=(EditText)findViewById(R.id.userPassword);
        btnLogin=(Button)findViewById(R.id.userLogin);
        loginProgress=(ProgressBar)findViewById(R.id.Loginpg);
        loginProgress.setVisibility(View.INVISIBLE);
        mAuth=FirebaseAuth.getInstance();
        HomeActivity=new Intent(this,com.example.ekisan.Activities.Home1.class);
        loginPhoto=(ImageView)findViewById(R.id.userPhoto);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                final String mail=userMail.getText().toString();
                final String password=userPassword.getText().toString();
                if(mail.isEmpty()||password.isEmpty())
                {

                    showMessage("Enter all the fields");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }
                else
                {

                    signIn(mail,password);
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();


                }
                else
                {

                    showMessage(task.getException().toString());

                }

            }
        });

    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null)
        {
            //user is already connected so we need to redirect him to home page
            updateUI();

        }



    }
}
