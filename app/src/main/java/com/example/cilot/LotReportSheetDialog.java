package com.example.cilot;

import android.graphics.Color;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;




public class LotReportSheetDialog extends BottomSheetDialogFragment {
    public static int START_TIME = 6;
    public static int END_TIME = 18;
    public static int OPEN = 1;
    public static int GRAPH_ENTRIES = 13;
    public static int MODERATE = 2;
    public static int FULL = 3;

    BarChart barChart;
    TextView tvStatus;
    TextView tvLot;
    TextView tvDay;

    DatabaseReference database;
    DatabaseReference currentStatusDatabase;
    DatabaseReference pollDatabase;
    DatabaseReference respondantsDatabase;
    DatabaseReference currentStatusTimeDatabase;
    DatabaseReference coneVisiblity;

    Button btnSubmitPoll;
    RadioGroup radioGroupPoll;
    RadioButton radioButtonSelected;

    ImageButton cautionButton;
    boolean cautionOn = false;

    Calendar calendar = Calendar.getInstance();
    int currDay;
    String dbDay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.lot_report,container,false);

        barChart = (BarChart) view.findViewById(R.id.barChart);

        tvStatus = view.findViewById(R.id.tvStatus);
        tvLot = view.findViewById(R.id.tvLot);

        radioGroupPoll = view.findViewById(R.id.poll);
        btnSubmitPoll = view.findViewById(R.id.btnSubmitPoll);
        cautionButton = view.findViewById(R.id.caution_button);

        currDay = calendar.get(Calendar.DAY_OF_WEEK);
        tvDay = view.findViewById(R.id.currentDay);
        dbDay = null;

        String lotName = getArguments().getString("params");

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

        tvDay.setText(dbDay);
        currentStatusDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("current_status").child("polls");
        pollDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("current_status").child("polls");
        respondantsDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("current_status").child("respondants");
        currentStatusTimeDatabase = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("current_status").child("time");

        coneVisiblity = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName).child("cautionVisible");
        coneVisiblity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true"))
                {
                    cautionOn = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cautionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coneVisiblity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue().toString().equals("false"))
                        {
                            coneVisiblity.setValue("true");
                            tvStatus.setText("CLOSED");
                            tvStatus.setTextColor(Color.parseColor("#FFA200"));
                            cautionOn = true;
                        }
                        else
                        {
                            Toast.makeText(getContext(),"Cone Already Up",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnSubmitPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        final int radioID = radioGroupPoll.getCheckedRadioButtonId();
        radioButtonSelected = getView().findViewById(radioID);

        Toast.makeText(getContext(),"Selected " + radioButtonSelected.getText(), Toast.LENGTH_SHORT).show();

        pollDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long pollCount;
                    if (dataSnapshot.exists())
                    {
                        pollCount =(dataSnapshot.getValue(Long.class));

                        switch(radioButtonSelected.getText().toString())
                        {
                            case "Open":
                                pollDatabase.setValue(pollCount + OPEN);
                                break;
                            case "Moderate":
                                pollDatabase.setValue(pollCount + MODERATE);
                                break;
                            default:
                                pollDatabase.setValue(pollCount + FULL);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

            respondantsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long resCounter;

                        if (dataSnapshot.exists())
                        {
                            resCounter = (dataSnapshot.getValue(Long.class));
                            respondantsDatabase.setValue(resCounter + 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        database = FirebaseDatabase.getInstance().getReference().child("lots").child(lotName);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentRes = dataSnapshot.child("current_status").child("respondants").getValue().toString();
                float currentResFloat = Float.parseFloat(currentRes);
                String currentPolls = dataSnapshot.child("current_status").child("polls").getValue().toString();
                float currentPollsFloat = Float.parseFloat(currentPolls);
                float currentAvg = currentPollsFloat/currentResFloat;




                String[] times = {"12am", "1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am", "12pm",
                        "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm"};
                String[] baseDataString = new String[GRAPH_ENTRIES];
                Float[] baseDataFloat = new Float[GRAPH_ENTRIES];

                //retrieve data from database to put into graphs
                String lotName = dataSnapshot.child("lot_name").getValue().toString();
                for(int i = 0; i < GRAPH_ENTRIES; i++)
                {
                    baseDataString[i] = dataSnapshot.child(dbDay).child(times[i+START_TIME]).getValue().toString();
                    baseDataFloat[i] = Float.parseFloat(baseDataString[i]);
                }

                //replace current bar hour in graph with live data
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);



                if(currentHour > START_TIME-1 && currentHour < END_TIME+1) {
                    baseDataFloat[currentHour-START_TIME] = currentAvg;
                }


                //place data into graph
                for (int i = 0; i < 13; i++) {
                    barEntries.add(new BarEntry(i, baseDataFloat[i]));
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "0");

                BarData data = new BarData(barDataSet);
                barChart.setData(data);

                //highlight current hour
                barDataSet.setHighlightEnabled(true);
                barDataSet.setHighLightColor(Color.BLACK);

                if(currentHour > START_TIME-1 && currentHour < END_TIME+1)
                {
                    barChart.highlightValue(currentHour-START_TIME,0,false);
                }

                //disable values
                barDataSet.setDrawValues(false);

                //disable description
                Description description = barChart.getDescription();
                description.setEnabled(false);

                //barChart.setFitBars(true);
                barChart.getAxisLeft().setAxisMinimum(1f);
                barChart.getAxisLeft().setAxisMaximum(3f);

                YAxis left = barChart.getAxisLeft();
                left.setDrawLabels(false); // no axis labels
                left.setDrawAxisLine(false); // no axis line
                left.setDrawGridLines(false); // no grid lines
                left.setDrawZeroLine(true); // draw a zero line
                barChart.getAxisRight().setEnabled(false);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);


                //FORMAT x-axis
                final ArrayList<String> xLabel = new ArrayList<>();
                xLabel.add("6AM");
                xLabel.add("7AM");
                xLabel.add("8AM");
                xLabel.add("9AM");
                xLabel.add("10AM");
                xLabel.add("11AM");
                xLabel.add("12PM");
                xLabel.add("1PM");
                xLabel.add("2PM");
                xLabel.add("3PM");
                xLabel.add("4PM");
                xLabel.add("5PM");
                xLabel.add("6PM");


                class MyXAxisFormatter extends ValueFormatter {

                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return xLabel.get((int)value);
                    }
                }
                xAxis.setValueFormatter(new MyXAxisFormatter());

                //disable legend
                Legend legend = barChart.getLegend();
                legend.setEnabled(false);

                //set the data
                BarData theData = new BarData(barDataSet);
                barChart.setData(theData);


                ////////////////////////////////////////
                //cuurent status


                float polls = Float.parseFloat(dataSnapshot.child("current_status").child("polls").getValue().toString());
                int respondants = Integer.parseInt((dataSnapshot.child("current_status").child("respondants").getValue().toString()));

                float currentStatus = polls/respondants;

                int tvColor = Color.GREEN;
                String tvCurrentStatus = "OPEN";



                //Grab most recent data from base data
                String currentStatusTime = dataSnapshot.child("current_status").child("time").getValue().toString();
                int time = Integer.parseInt(currentStatusTime);

                float basePoll;
                //check if currentHour is in between start time and end time, if not set default to open
                if(currentHour < START_TIME || currentHour > END_TIME)
                    basePoll = OPEN;
                else
                    basePoll = Float.parseFloat(dataSnapshot.child(dbDay).child(times[currentHour-START_TIME]).getValue().toString());


                if(currentHour != time)
                {
                    currentStatusTimeDatabase.setValue(currentHour);
                    pollDatabase.setValue(basePoll);
                    respondantsDatabase.setValue(1);
                }

                //Set current status textviews
                if(cautionOn)
                {
                    tvCurrentStatus = "CLOSED";
                    tvColor = Color.parseColor("#FFA200");
                }
                else
                {
                    if (currentStatus >= 1 && currentStatus <= 1.4)
                    {
                        tvCurrentStatus = "OPEN";
                        tvColor = Color.GREEN;
                    }
                    else if (currentStatus > 1.4 && currentStatus < 2.4)
                    {
                        tvCurrentStatus = "MODERATE";
                        tvColor = Color.YELLOW;
                    }
                    else if (currentStatus >= 2.4 && currentStatus <= 3)
                    {
                        tvCurrentStatus = "FULL";
                        tvColor = Color.RED;
                    }
                    else
                    {
                        tvColor = Color.GREEN;
                        tvCurrentStatus = "OPEN";
                    }
                }


                tvLot.setText(lotName);

                tvStatus.setText(tvCurrentStatus);
                tvStatus.setTextColor(tvColor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}



