package com.backup.walle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    public static final String TAG = "TAG";
    private EditText Password;
    private EditText Email;
    private EditText Name;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.up_sign);

        Password = findViewById(R.id.Password);
        Email = findViewById(R.id.Email);
        Name = findViewById(R.id.Name);
        Button Signup = findViewById(R.id.Signup);
        fAuth = FirebaseAuth.getInstance();
        ImageButton Back = findViewById(R.id.Back);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email= Email.getText().toString().trim();
                String password=Password.getText().toString().trim();
                final String name =Name.getText().toString().trim();
                //Requirements in account creation, will produce errors if not met
                if(TextUtils.isEmpty(name)){
                    Name.setError("Name cannot be blank");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Email.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Password.setError("Password required");
                    return;
                }
                if(password.length()< 6){
                    Password.setError("Password must be at least 6 Characters long");
                    return;
                }

                //Registering the User in FireBase and storing the Name and Email in Firestore database
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                           Map<String, Object> User = new HashMap<>();
                           User.put("Name",name);
                           User.put("E-mail", email);
                           User.put("Klok",null);
                           User.put("Wateren",false);

                           if(fAuth.getCurrentUser()!=null){
                               String userId = fAuth.getUid();
                               db.collection("Users").document(userId)
                                       .set(User);
                           }
                           startActivity( new Intent(getApplicationContext(),LogIn.class));
                       }else{
                           Toast.makeText(SignUp.this,"Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                       }
                    }
                });
            }
        });
        //Sending the User back to the LogIn screen if pressed
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
            }
        });
    }
}
