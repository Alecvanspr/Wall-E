package com.backup.walle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {
    private Button button;

    private int currentApiVersion;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Setting the activity in full screen
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // Checks Api version, will only work with Api higher then KitKat
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            //Code to make sure when the Volume Buttons are pressed that it it exits fullscreen but goes back into fullscreen
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

        Fragment fragment = new DashBoard();
        fragmentTransaction.add(R.id.fragment_layout,fragment);

        fragmentTransaction.commit();

        BottomNavigationView BottomNav = findViewById(R.id.BottomNav);
        BottomNav.setOnNavigationItemSelectedListener(BottomNavListener);
    }
    //Sending user to the fragment of choice when clicked
    private BottomNavigationView.OnNavigationItemSelectedListener BottomNavListener = new
            BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment thisFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_Dashboard:
                            thisFragment = new DashBoard();
                            break;
                        case R.id.nav_Profile:
                            thisFragment = new Profile();
                            break;
                        case R.id.nav_Settings:
                            thisFragment = new Settings();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,thisFragment).commit();
                    return true;}
    };

    //Logout button
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LogIn.class));
        finish();
    }
    //Dit is de knop naar de temperatuur
    public void temperature(View view){
        Fragment anotherFragment = new Temperature();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout,anotherFragment,anotherFragment.getTag()).commit();
    }
    //Watertab knop
    public void water(View view){
        startActivity(new Intent(getApplicationContext(), Watertab.class));
        finish();
    }
}
