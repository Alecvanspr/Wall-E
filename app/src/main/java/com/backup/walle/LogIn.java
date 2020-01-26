package com.backup.walle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private Button Login;
    private int Attempt;
    private TextView Signup;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_log);

        Email = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Password);
        Login = (Button) findViewById(R.id.Login);
        Signup = (TextView) findViewById(R.id.SignUp);
        fAuth = FirebaseAuth.getInstance();

        //Checking if the user is already signed in
        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), Main.class));
            finish();
        }

        //Sending the user to the Dashboard if successfully logged in
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Email.getText().toString().trim();
                String password=Password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Email.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Password.setError("Password required");
                    return;
                }

                //Authenticating the User
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), Main.class));
                        }else{
                            Toast.makeText(LogIn.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Attempt++;
                            if(Attempt==5){
                                Login.setEnabled(false);
                            }
                        }
                    }
                });
            }
            });
        //Sending the User to Sign Up page when clicked
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}

