package com.backup.walle;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Watertab extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    //final TextView Tijd = view.findViewById(R.id.Tijd);
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String UserId = FirebaseAuth.getInstance().getUid();
    private Button button;
    private Button waterenknp;
    long totaal;
    long mTimeLeftInMillis;
    int getal1 = 600000000;
    int uur;
    int minuten;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private Button mButtonStartPause;
    private Button mButtonReset;


    private boolean mTimerRunning;

    private long mEndTime;

    Calendar rightNow = Calendar.getInstance();
    int currentHourIn24Format = (rightNow.get(Calendar.HOUR_OF_DAY))*3600; // return the hour in 24 hrs format (ranging from 0-23)
    int currentMinute = (rightNow.get(Calendar.MINUTE))*60; // return the hour in 24 hrs format (ranging from 0-23)
    int currentHourIn12Format = rightNow.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11)
    public int counter;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watertab);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String UserId = FirebaseAuth.getInstance().getUid();

        button= (Button) findViewById(R.id.waterknp);
        textView = (TextView) findViewById(R.id.text_view_countdown);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = db.collection("Users").document(UserId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        String tijd = ("Tijd: " + documentSnapshot.getLong("Tijd"));
                    }
                });
                new CountDownTimer(600000000, 1) {
                    public void onTick(long millisUntilFinished) {
                        if (((millisUntilFinished / 1000) % 59) < 9) {
                            textView.setText((String.valueOf(millisUntilFinished / 36000000)) + ":" + (String.valueOf((millisUntilFinished / 60000) % 59)) + ":" + (String.valueOf(((millisUntilFinished / 1000) % 59)))); //1000 laat de seconden zien
                        } else {
                            textView.setText((String.valueOf(millisUntilFinished / 36000000)) + ":" + (String.valueOf((millisUntilFinished / 60000) % 59)) + ":0" + (String.valueOf(((millisUntilFinished / 1000) % 59)))); //1000 laat de seconden zien
                        }
                        counter--;
                    }

                    public void onFinish () {
                        textView.setText("FINISH!!");
                    }
                }.start();
            };
    });
/*
        DocumentReference documentReference = db.collection("Users").document(UserId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String tijd = ("Tijd: "+ documentSnapshot.getString("Tijd"));
                long tijd2 = Long.parseLong(tijd);
            }
        });
*/


        final Switch simpleSwitch = (Switch) findViewById(R.id.KlokAan);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);


        //Dit is voor de aan uit knop voor de klok.
        boolean KlokKnop = false;
        Boolean switchState = simpleSwitch.isChecked(); //Dit checkt of de timer klok aan of uit is

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DocumentReference documentReference = db.collection("Users").document(UserId);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            TextView textView = (TextView) findViewById(R.id.textView);
                            textView.setText("Hour: " + (((documentSnapshot.getLong("Uur")))) + " Minute: " + (((documentSnapshot.getLong("Minuten")))));
                        }
                    });
                } else {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText("Geen tijd ingesteld");
                }
            }
        });
        button = (Button) findViewById(R.id.GATERUG);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GATERUG();
            }
        });

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        Button button = (Button) findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });//Dit opent de tijdfragment
    }
    @Override
    public void onTimeSet (TimePicker view,int hourOfDay, int minute){ //Dit haalt de geselecteerde tijd tevoorschjn
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Hour: " + hourOfDay + " Minute: " + minute);
        uur = hourOfDay;
        minuten=minute;
        long START_TIME_IN_MILLIS =(((minuten)+((uur)))); //Dit werkt en update het in
        totaal = ((currentMinute)+(currentHourIn24Format));
        long mTimeLeftInMillis = START_TIME_IN_MILLIS-totaal;

        Map<String, Object> User = new HashMap<>();
        User.put("Uur", hourOfDay);
        User.put("minuten",minute);
        db.collection("Users").document(UserId)
                .update(User);
    }
    public  void onFinish(){
        textView.setText("FINISH!!");
    }
    //dit is voor het updaten voor de tijd.
    public void GATERUG() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

}