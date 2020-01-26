package com.backup.walle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        final TextView Name = view.findViewById(R.id.Profilename);
        final TextView Email = view.findViewById(R.id.Profileemail);
        final TextView Modelnumber = view.findViewById(R.id.Profilemodelnumber);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String UserId = FirebaseAuth.getInstance().getUid();


        DocumentReference documentReference = db.collection("Users").document(UserId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Name.setText("Name: "+ documentSnapshot.getString("Name"));
                Email.setText("E-mail: "+ documentSnapshot.getString("E-mail"));
            }
        });
        DocumentReference df = db.collection("Users").document(UserId).collection("System Information").document("System");
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Modelnumber.setText("Modelnumber: "+ documentSnapshot.getString("Serialnumber"));
            }
        });
        return view;
    }
}
