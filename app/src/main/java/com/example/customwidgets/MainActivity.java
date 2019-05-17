package com.example.customwidgets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.customwidgetslibrary.calendar.ITSCustomCalendarView;

public class MainActivity extends AppCompatActivity {
    private ITSCustomCalendarView itsCustomCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itsCustomCalendarView = findViewById(R.id.its_calendar);

        itsCustomCalendarView.setShouldDrawDaysHeader(true);
        itsCustomCalendarView.setUsingThreeLetterForWeek(true);
        itsCustomCalendarView.shouldDrawLineDividerUnderWeekDaysHeader(true);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderHeight(5);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderColor(getResources().getColor(R.color.yellow));
        itsCustomCalendarView.setRtl(false);
        itsCustomCalendarView.setDisplayOtherMonthDays(false); //popraviti, ne sme da prelazi u sledeci red ako je trenutni ceo ispunjen
    }
}
