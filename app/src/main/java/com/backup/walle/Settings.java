package com.backup.walle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Settings extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        final EditText Username = view.findViewById(R.id.Username);
        final EditText Address = view.findViewById(R.id.Adress);
        Button Update = view.findViewById(R.id.Update);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String UserId = FirebaseAuth.getInstance().getUid();

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = Username.getText().toString();
                final String address = Address.getText().toString();

                Map<String, Object> User = new HashMap<>();
                User.put("Username",username);
                User.put("Address", address);
                db.collection("Users" ).document(UserId)
                        .update(User);
            }
        });
        return view;
    }
}
