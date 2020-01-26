package com.backup.walle;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DashBoard extends AppCompatActivity {

    private EditText Username;
    private EditText Address;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_dash);
        Database();
    }
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LogIn.class));
        finish();
    }
    public void Database() {
        Username = findViewById(R.id.Username);
        Address = findViewById(R.id.Adress);
        Button Update = findViewById(R.id.Update);
        Button System = findViewById(R.id.Test);
        Button Sensor = findViewById(R.id.Test);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Getting user id and assigning it to a string
        if (fAuth.getCurrentUser() != null) {
            final String userId = fAuth.getUid();

            //Updating user specific information adding a Username and Address into Firebase
            Update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = Username.getText().toString();
                    final String address = Address.getText().toString();

                    Map<String, Object> User = new HashMap<>();
                    User.put("Username", username);
                    User.put("Address", address);
                    db.collection("Users").document(userId)
                            .update(User);
                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                }
            });
            System.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> User = new HashMap<>();
                    User.put("Serialnumber", "5FRPHL3MY6MX");
                    User.put("System address","test");
                    db.collection("Users").document(userId).collection("System Information").document("System")
                            .set(User);

                }
            });
            Sensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> User = new HashMap<>();
                    User.put("Sensor number", "DFVZ4VUWXDRS");
                    User.put("Sensor type", "Temperature");
                    db.collection("Users").document(userId).collection("System Information").document("Sensors")
                            .set(User);
                }
            });
        }
    }
}
