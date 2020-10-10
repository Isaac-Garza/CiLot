package com.example.cilot;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    public static int START_TIME = 6;
    public static int END_TIME = 18;
    public static double OPEN = 1.6;
    public static double MODERATE = 2.4;
    public static double FULL = 3;
    public static int NUMBER_OF_LOTS = 11;
    public static int CURRENT_HOUR;
    public static int GREEN = Color.parseColor("#69ff73");
    public static int YELLOW = Color.parseColor("#f2ff5e");
    public static int RED = Color.parseColor("#fc3d3d");


    private DrawerLayout drawer;

    DatabaseReference database;
    DatabaseReference buttonColors;
    DatabaseReference buttonColors2;
    DatabaseReference coneVisibility;
    DatabaseReference downVote;

    ImageButton coneImage_a1;
    ImageButton coneImage_a2;
    ImageButton coneImage_a3;
    ImageButton coneImage_a4;
    ImageButton coneImage_a5;
    ImageButton coneImage_a6;
    ImageButton coneImage_a7;
    ImageButton coneImage_a8;
    ImageButton coneImage_a9;
    ImageButton coneImage_a10;
    ImageButton coneImage_a11;

    NavigationView navigationView;

    DatabaseReference downVoteCount;
    DatabaseReference updatePoints;

    GoogleSignInAccount account;

    String currentLot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        CURRENT_HOUR = calendar.get(Calendar.HOUR_OF_DAY);

        coneImage_a1 = findViewById(R.id.coneLot1);
        coneImage_a2 = findViewById(R.id.coneLot2);
        coneImage_a3 = findViewById(R.id.coneLot3);
        coneImage_a4 = findViewById(R.id.coneLot4);
        coneImage_a5 = findViewById(R.id.coneLot5);
        coneImage_a6 = findViewById(R.id.coneLot6);
        coneImage_a7 = findViewById(R.id.coneLot7);
        coneImage_a8 = findViewById(R.id.coneLot8);
        coneImage_a9 = findViewById(R.id.coneLot9);
        coneImage_a10 = findViewById(R.id.coneLot10);
        coneImage_a11 = findViewById(R.id.coneLot11);

        Button button_a1 = findViewById(R.id.button_a1);
        Button button_a2 = findViewById(R.id.button_a2);
        Button button_a3 = findViewById(R.id.button_a3);
        Button button_a4 = findViewById(R.id.button_a4);
        Button button_a5 = findViewById(R.id.button_a5);
        Button button_a6 = findViewById(R.id.button_a6);
        Button button_a7 = findViewById(R.id.button_a7);
        Button button_a8 = findViewById(R.id.button_a8);
        Button button_a9 = findViewById(R.id.button_a9);
        Button button_a10 = findViewById(R.id.button_a10);
        Button button_a11 = findViewById(R.id.button_a11);

        final Button[] mapButtons = {button_a1, button_a2, button_a3, button_a4, button_a5, button_a6,
                button_a7, button_a8, button_a9, button_a10, button_a11};

        button_a1.setOnClickListener(this);
        button_a2.setOnClickListener(this);
        button_a3.setOnClickListener(this);
        button_a4.setOnClickListener(this);
        button_a5.setOnClickListener(this);
        button_a6.setOnClickListener(this);
        button_a7.setOnClickListener(this);
        button_a8.setOnClickListener(this);
        button_a9.setOnClickListener(this);
        button_a10.setOnClickListener(this);
        button_a11.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

        //navigationView.getMenu().getItem(1).setIcon(R.drawable.car_green);

        //update time in database
        database = FirebaseDatabase.getInstance().getReference().child("time");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(CURRENT_HOUR != Integer.parseInt(dataSnapshot.getValue().toString()))
                {
                    database.setValue(CURRENT_HOUR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String[] lotNames = {"A1", "A2", "A3", "A4", "A5", "A6","A7", "A8", "A9", "A10", "A11"};
        for(int k = 0; k < lotNames.length; k++)
        {
            changeButtonColors(mapButtons[k], lotNames[k]);
            setConeVisibility(lotNames[k]);
        }


        //change button colors
        //buttonColors = FirebaseDatabase.getInstance().getReference().child("lots");
        /*buttonColors.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] lotNames = {"A1", "A2", "A3", "A4", "A5", "A6","A7", "A8", "A9", "A10", "A11"};
                for(int k = 0; k < lotNames.length; k++)
                {
                    changeButtonColors(mapButtons[k], lotNames[k]);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }


    private void setConeVisibility(final String lotName) {
        coneVisibility = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("cautionVisible");
        coneVisibility.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int visibility;
                if(dataSnapshot.getValue().toString().equals("true"))
                {
                    visibility = (View.VISIBLE);
                }
                else
                {
                    visibility = (View.INVISIBLE);
                }

                switch(lotName)
                {
                    case "A1":
                        coneImage_a1.setVisibility(visibility);
                        break;
                    case "A2":
                        coneImage_a2.setVisibility(visibility);
                        break;
                    case "A3":
                        coneImage_a3.setVisibility(visibility);
                        break;
                    case "A4":
                        coneImage_a4.setVisibility(visibility);
                        break;
                    case "A5":
                        coneImage_a5.setVisibility(visibility);
                        break;
                    case "A6":
                        coneImage_a6.setVisibility(visibility);
                        break;
                    case "A7":
                        coneImage_a7.setVisibility(visibility);
                        break;
                    case "A8":
                        coneImage_a8.setVisibility(visibility);
                        break;
                    case "A9":
                        coneImage_a9.setVisibility(visibility);
                        break;
                    case "A10":
                        coneImage_a10.setVisibility(visibility);
                        break;
                    case "A11":
                        coneImage_a11.setVisibility(visibility);
                        break;
                    default:
                        System.out.println("ERROR!!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lot_a1:
                Bundle bundleA1 = new Bundle();
                bundleA1.putString("params", "A1");
                LotReportSheetDialog bottomSheetA1 = new LotReportSheetDialog();
                bottomSheetA1.setArguments(bundleA1);
                bottomSheetA1.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;

            case R.id.lot_a2:
                Bundle bundleA2 = new Bundle();
                bundleA2.putString("params", "A2");

                LotReportSheetDialog bottomSheetA2 = new LotReportSheetDialog();
                bottomSheetA2.setArguments(bundleA2);
                bottomSheetA2.show(getSupportFragmentManager(), "exampleBottomSheet");

                break;
            case R.id.lot_a3:
                Bundle bundleA3 = new Bundle();
                bundleA3.putString("params", "A3");

                LotReportSheetDialog bottomSheetA3 = new LotReportSheetDialog();
                bottomSheetA3.setArguments(bundleA3);
                bottomSheetA3.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a4:
                Bundle bundleA4 = new Bundle();
                bundleA4.putString("params","A4");
                LotReportSheetDialog bottomSheetA4 = new LotReportSheetDialog();
                bottomSheetA4.setArguments(bundleA4);
                bottomSheetA4.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a5:
                Bundle bundleA5 = new Bundle();
                bundleA5.putString("params","A5");
                LotReportSheetDialog bottomSheetA5 = new LotReportSheetDialog();
                bottomSheetA5.setArguments(bundleA5);
                bottomSheetA5.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a6:
                Bundle bundleA6 = new Bundle();
                bundleA6.putString("params","A6");
                LotReportSheetDialog bottomSheetA6 = new LotReportSheetDialog();
                bottomSheetA6.setArguments(bundleA6);
                bottomSheetA6.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a7:
                Bundle bundleA7 = new Bundle();
                bundleA7.putString("params","A7");
                LotReportSheetDialog bottomSheetA7 = new LotReportSheetDialog();
                bottomSheetA7.setArguments(bundleA7);
                bottomSheetA7.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a8:
                Bundle bundleA8 = new Bundle();
                bundleA8.putString("params","A8");
                LotReportSheetDialog bottomSheetA8 = new LotReportSheetDialog();
                bottomSheetA8.setArguments(bundleA8);
                bottomSheetA8.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a9:
                Bundle bundleA9 = new Bundle();
                bundleA9.putString("params","A9");
                LotReportSheetDialog bottomSheetA9 = new LotReportSheetDialog();
                bottomSheetA9.setArguments(bundleA9);
                bottomSheetA9.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a10:
                Bundle bundleA10 = new Bundle();
                bundleA10.putString("params","A10");
                LotReportSheetDialog bottomSheetA10 = new LotReportSheetDialog();
                bottomSheetA10.setArguments(bundleA10);
                bottomSheetA10.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.lot_a11:
                Bundle bundleA11 = new Bundle();
                bundleA11.putString("params","A11");
                LotReportSheetDialog bottomSheetA11 = new LotReportSheetDialog();
                bottomSheetA11.setArguments(bundleA11);
                bottomSheetA11.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.profile:
                account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if(account == null)
                {
                    Intent intent = new Intent(MainActivity.this, com.example.cilot.profile_login.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, com.example.cilot.profile_icons.class);
                    startActivity(intent);
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.button_a1:
                Bundle bundleA1 = new Bundle();
                bundleA1.putString("params","A1");
                LotReportSheetDialog bottomSheetA1 = new LotReportSheetDialog();
                bottomSheetA1.setArguments(bundleA1);
                bottomSheetA1.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a2:
                Bundle bundleA2 = new Bundle();
                bundleA2.putString("params","A2");
                LotReportSheetDialog bottomSheetA2 = new LotReportSheetDialog();
                bottomSheetA2.setArguments(bundleA2);
                bottomSheetA2.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a3:
                Bundle bundleA3 = new Bundle();
                bundleA3.putString("params","A3");
                LotReportSheetDialog bottomSheetA3 = new LotReportSheetDialog();
                bottomSheetA3.setArguments(bundleA3);
                bottomSheetA3.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a4:
                Bundle bundleA4 = new Bundle();
                bundleA4.putString("params","A4");
                LotReportSheetDialog bottomSheetA4 = new LotReportSheetDialog();
                bottomSheetA4.setArguments(bundleA4);
                bottomSheetA4.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a5:
                Bundle bundleA5 = new Bundle();
                bundleA5.putString("params","A5");
                LotReportSheetDialog bottomSheetA5 = new LotReportSheetDialog();
                bottomSheetA5.setArguments(bundleA5);
                bottomSheetA5.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a6:
                Bundle bundleA6 = new Bundle();
                bundleA6.putString("params","A6");
                LotReportSheetDialog bottomSheetA6 = new LotReportSheetDialog();
                bottomSheetA6.setArguments(bundleA6);
                bottomSheetA6.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a7:
                Bundle bundleA7 = new Bundle();
                bundleA7.putString("params","A7");
                LotReportSheetDialog bottomSheetA7 = new LotReportSheetDialog();
                bottomSheetA7.setArguments(bundleA7);
                bottomSheetA7.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a8:
                Bundle bundleA8 = new Bundle();
                bundleA8.putString("params","A8");
                LotReportSheetDialog bottomSheetA8 = new LotReportSheetDialog();
                bottomSheetA8.setArguments(bundleA8);
                bottomSheetA8.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a9:
                Bundle bundleA9 = new Bundle();
                bundleA9.putString("params","A9");
                LotReportSheetDialog bottomSheetA9 = new LotReportSheetDialog();
                bottomSheetA9.setArguments(bundleA9);
                bottomSheetA9.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a10:
                Bundle bundleA10 = new Bundle();
                bundleA10.putString("params","A10");
                LotReportSheetDialog bottomSheetA10 = new LotReportSheetDialog();
                bottomSheetA10.setArguments(bundleA10);
                bottomSheetA10.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
            case R.id.button_a11:
                Bundle bundleA11 = new Bundle();
                bundleA11.putString("params","A11");
                LotReportSheetDialog bottomSheetA11 = new LotReportSheetDialog();
                bottomSheetA11.setArguments(bundleA11);
                bottomSheetA11.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        currentLot = getCurrentLot(v);
        popup.inflate(R.menu.cone_menu);
        popup.setForceShowIcon(true);
        popup.show();
    }

    public String getCurrentLot(View v)
    {
        String returnLotName = "";
        switch (v.getId())
        {
            case R.id.coneLot1:
                returnLotName = "A1";
                break;
            case R.id.coneLot2:
                returnLotName = "A2";
                break;
            case R.id.coneLot3:
                returnLotName = "A3";
                break;
            case R.id.coneLot4:
                returnLotName = "A4";
                break;
            case R.id.coneLot5:
                returnLotName = "A5";
                break;
            case R.id.coneLot6:
                returnLotName = "A6";
                break;
            case R.id.coneLot7:
                returnLotName = "A7";
                break;
            case R.id.coneLot8:
                returnLotName = "A8";
                break;
            case R.id.coneLot9:
                returnLotName = "A9";
                break;
            case R.id.coneLot10:
                returnLotName = "A10";
                break;
            case R.id.coneLot11:
                returnLotName = "A11";
                break;

        }
        return returnLotName;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        downVote = FirebaseDatabase.getInstance().getReference().child("lots").child(currentLot);
        updatePoints = FirebaseDatabase.getInstance().getReference().child("users").child("111802371807776977889").child("points");
        updatePoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentPoints = dataSnapshot.getValue().toString();
                float addPoints = 0;
                addPoints = Float.parseFloat(currentPoints);
                addPoints+=5;
                updatePoints.setValue(addPoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        switch(item.getItemId())
        {
            case R.id.cone_option1:
                Toast.makeText(this, "Upvoted +5", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cone_option2:
                Toast.makeText(this, "Downvoted +5", Toast.LENGTH_SHORT).show();

                downVote.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float downVoteValue = Float.parseFloat(dataSnapshot.child("DownVote").getValue().toString());
                        if(downVoteValue < 3)
                        {
                            downVoteValue++;
                            downVote.child("DownVote").setValue(downVoteValue);
                        }
                        else
                        {
                            downVoteValue = 0;
                            downVote.child("DownVote").setValue(downVoteValue);
                            updateConeVisiblility(currentLot);
                            downVote.child("cautionVisible").setValue("false");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            default:
                return false;
        }
    }

    public void updateConeVisiblility(String lotName)
    {

        switch(lotName)
        {
            case "A1":
                coneImage_a1.setVisibility(View.INVISIBLE);
                break;
            case "A2":
                coneImage_a2.setVisibility(View.INVISIBLE);
                break;
            case "A3":
                coneImage_a3.setVisibility(View.INVISIBLE);
                break;
            case "A4":
                coneImage_a4.setVisibility(View.INVISIBLE);
                break;
            case "A5":
                coneImage_a5.setVisibility(View.INVISIBLE);
                break;
            case "A6":
                coneImage_a6.setVisibility(View.INVISIBLE);
                break;
            case "A7":
                coneImage_a7.setVisibility(View.INVISIBLE);
                break;
            case "A8":
                coneImage_a8.setVisibility(View.INVISIBLE);
                break;
            case "A9":
                coneImage_a9.setVisibility(View.INVISIBLE);
                break;
            case "A10":
                coneImage_a10.setVisibility(View.INVISIBLE);
                break;
            case "A11":
                coneImage_a11.setVisibility(View.INVISIBLE);
                break;
            default:
                System.out.println("ERROR!!!");
        }
    }
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void changeButtonColors(final Button button, final String lotNameParam){
        buttonColors2 = FirebaseDatabase.getInstance().getReference().child("lots").child(lotNameParam);

        buttonColors2.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //update current statuses
                    List lotNames = Arrays.asList(new String[] { "profile", "A1", "A2", "A3", "A4", "A5", "A6","A7", "A8", "A9", "A10", "A11"});
                    String[] times = {"12am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am", "12pm",
                            "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"};
                    Calendar calendar = Calendar.getInstance();
                    int currDay = calendar.get(Calendar.DAY_OF_WEEK);
                    String dbDay = null;
                    switch(currDay)
                    {
                        case Calendar.SUNDAY:
                            dbDay = "sunday";
                            break;
                        case Calendar.MONDAY:
                            dbDay = "monday";
                            break;
                        case Calendar.TUESDAY:
                            dbDay = "tuesday";
                            break;
                        case Calendar.WEDNESDAY:
                            dbDay = "wednesday";
                            break;
                        case Calendar.THURSDAY:
                            dbDay = "thursday";
                            break;
                        case Calendar.FRIDAY:
                            dbDay = "friday";
                            break;
                        case Calendar.SATURDAY:
                            dbDay = "saturday";
                            break;
                    }
                    String currentStatusTime = dataSnapshot.child("current_status").child("time").getValue().toString();
                    int time = Integer.parseInt(currentStatusTime);
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    DatabaseReference pollDatabase;
                    DatabaseReference currentStatusTimeDatabase;
                    DatabaseReference respondantsDatabase;
                    respondantsDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotNameParam).child("current_status").child("respondants");
                    currentStatusTimeDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotNameParam).child("current_status").child("time");
                    pollDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotNameParam).child("current_status").child("polls");
                    double basePoll;
                    //check if currentHour is in between start time and end time, if not set default to open
                    if(currentHour < START_TIME || currentHour > END_TIME)
                        basePoll = OPEN;
                    else
                        basePoll = Double.parseDouble((dataSnapshot.child(dbDay).child(times[currentHour]).getValue().toString()));
                    if(currentHour != time)
                    {
                        currentStatusTimeDatabase.setValue(currentHour);
                        pollDatabase.setValue(basePoll);
                        respondantsDatabase.setValue(1);
                    }

                    //change colors
                    int btnColor = GREEN;
                    int carColor = R.drawable.car_green;

                    float polls = Float.parseFloat(dataSnapshot.child("current_status").child("polls").getValue().toString());
                    int respondants = Integer.parseInt((dataSnapshot.child("current_status").child("respondants").getValue().toString()));

                    float currentStatus = polls/respondants;

                    if(currentStatus <= OPEN) {
                        btnColor = GREEN;
                        button.setTextColor(Color.BLACK);
                        carColor = R.drawable.car_green;
                    }
                    else if(currentStatus > OPEN && currentStatus < MODERATE) {
                        btnColor = YELLOW;
                        button.setTextColor(Color.BLACK);
                        carColor = R.drawable.car_yellow;
                    }
                    else if(currentStatus >= MODERATE && currentStatus <= FULL) {
                        btnColor = RED;
                        button.setTextColor(Color.WHITE);
                        carColor = R.drawable.car_red;
                    }

                    button.setBackgroundColor(btnColor);
                    navigationView.getMenu().getItem(lotNames.indexOf(lotNameParam)).setIcon(carColor);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //public void updateCurrentStatus
}