package com.backup.walle;


import android.app.TimePickerDialog;
import android.content.Intent;
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
    int uur;
    int minuten;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;


    private long mEndTime;
    Calendar rightNow = Calendar.getInstance();
    int currentHourIn24Format = (rightNow.get(Calendar.HOUR_OF_DAY))*3600; // return the hour in 24 hrs format (ranging from 0-23)
    int currentMinute = (rightNow.get(Calendar.MINUTE))*60; // return the hour in 24 hrs format (ranging from 0-23)
    int currentHourIn12Format = rightNow.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watertab);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String UserId = FirebaseAuth.getInstance().getUid();

        final Switch simpleSwitch = (Switch) findViewById(R.id.KlokAan);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        //Dit is voor de aan uit knop voor de klok.
        boolean KlokKnop = false;
        Boolean switchState = simpleSwitch.isChecked(); //Dit checkt of de timer klok aan of uit is

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Map<String, Object> User = new HashMap<>();
                    User.put("Klok", isChecked);
                    db.collection("Users").document(UserId)
                            .update(User);
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
        Button button = (Button) findViewById(R.id.button);
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
        uur = hourOfDay*3600;
        minuten=minute*60;
        long START_TIME_IN_MILLIS =(((minuten)+((uur)))); //Dit werkt en update het in
        totaal = ((currentMinute)+(currentHourIn24Format));
        long mTimeLeftInMillis = START_TIME_IN_MILLIS-totaal;

        Map<String, Object> User = new HashMap<>();
        User.put("Tijd", START_TIME_IN_MILLIS);
        db.collection("Users").document(UserId)
                .update(User);
    } //dit is voor het updaten voor de tijd.

    private void startTimer () {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override

            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateCountDownText () {

        DocumentReference documentReference = db.collection("Users").document(UserId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                waterenknp.setText("Tijd: "+ documentSnapshot.getString("Tijd"));

            }
        });
        int minutes = (int) (totaal) / 60;  //dit moet gekoppeld worden aan die ding in de void.Maar deze doet het niet
        int seconds = (int) (totaal) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }
    public void GATERUG() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
}


//dit is voor de momentele uur ETC.



//Dit werkt nog niet
//   public void Wateren_activity() {
//       final FirebaseFirestore db = FirebaseFirestore.getInstance();
//       final String UserId = FirebaseAuth.getInstance().getUid();
//       Map<String, Object> User = new HashMap<>();
//      User.put("Wateren", true);
//      db.collection("Users").document(UserId)
//              .update(User);
//  }

//    Button Waterknp = (Button) findViewById(R.id.waterknp); hier zit een fout. ik ben dit aan het fixen
//    button.setOnClickListener(new View.OnClickListener() {
//       @Override
//        public void onClick(View v) {
//          Wateren_activity();
//       }
//   });
//Dit is de knop voor de timer