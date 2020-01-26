package com.backup.walle;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Watertab extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private Button button;
    private Button waterenknp;
    int uur;
    int minuten;
    Calendar rightNow = Calendar.getInstance();
    int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
    int currentHourIn12Format = rightNow.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11)
    long START_TIME_IN_MILLIS = ((currentHourIn12Format)-currentHourIn24Format);
    long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watertab);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity
        }


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

        mTextViewCountDown =findViewById(R.id.text_view_countdown);

        mButtonStartPause =findViewById(R.id.button_start_pause);

        mButtonReset = findViewById(R.id.button_reset);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();

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
        public void onTimeSet (TimePicker view,int hourOfDay, int minute){
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("Hour: " + hourOfDay + " Minute: " + minute);
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
                    mTimerRunning = false;
                    mButtonStartPause.setText("Start");
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                    mButtonReset.setVisibility(View.VISIBLE);
                }
            }.start();

            mTimerRunning = true;
            mButtonStartPause.setText("pause");
            mButtonReset.setVisibility(View.INVISIBLE);
        }

        private void pauseTimer () {
            mCountDownTimer.cancel();
            mTimerRunning = false;
            mButtonStartPause.setText("Start");
            mButtonReset.setVisibility(View.VISIBLE);
        }

        private void resetTimer () {
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            updateCountDownText();
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setVisibility(View.VISIBLE);
        }

        private void updateCountDownText () {
            int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
            int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

            mTextViewCountDown.setText(timeLeftFormatted);
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