package com.sasaarsenovic.customwidgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sasaarsenovic.customwidgetslibrary.calendar.ITSCustomCalendarView;
import com.sasaarsenovic.customwidgetslibrary.calendar.IndicatorShapes;
import com.sasaarsenovic.customwidgetslibrary.calendar.event.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements ITSCustomCalendarView.ITSCustomCalendarViewListener {
    private ITSCustomCalendarView itsCustomCalendarView;
    private TextView monthName, left, right;
    private Calendar calendar = Calendar.getInstance();
    private Sensor accelerometer;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itsCustomCalendarView = findViewById(R.id.its_calendar);
        monthName = findViewById(R.id.month_name);
        left = findViewById(R.id.text_left);
        right = findViewById(R.id.text_right);

        itsCustomCalendarView.setListener(this);
//        itsCustomCalendarView.setLocale(TimeZone.getTimeZone("America/Los_Angeles"), Locale.US);

        itsCustomCalendarView.setBackgroundResource(R.drawable.gradient_test);
        itsCustomCalendarView.setShowGridView(false);
//        itsCustomCalendarView.setGridViewColor(getResources().getColor(R.color.yellow));
//        Date date = new Date();
//        date.setTime(1552258800000L);
//        //TODO: fix this function
//        itsCustomCalendarView.setCurrentDate(date);


//        itsCustomCalendarView.setCustomDaysForCustomDrawing(new Date[]{});

        itsCustomCalendarView.setShouldDrawDaysHeader(true);
        itsCustomCalendarView.setUsingThreeLetterForWeek(true);
        itsCustomCalendarView.setCalendarWeekDaysTextColor(getResources().getColor(R.color.midnight_blue));
        itsCustomCalendarView.setShouldPaintWeekendDaysForOtherMonths(false);
        itsCustomCalendarView.shouldDrawLineDividerUnderWeekDaysHeader(false);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderHeight(1);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderColor(getResources().getColor(R.color.white));

        itsCustomCalendarView.setColorForWeekendDays(getResources().getColor(R.color.red));

        itsCustomCalendarView.setCurrentDateTextColor(getResources().getColor(R.color.white));
        itsCustomCalendarView.shouldShowIndicatorForCurrentDay(true);
        itsCustomCalendarView.setCurrentDayIndicatorShape(IndicatorShapes.DOUBLE_CIRCLE);
        itsCustomCalendarView.setCurrentDayIndicatorColor(getResources().getColor(R.color.sweet_green));
        itsCustomCalendarView.setCurrentDayIndicatorStyle(Paint.Style.FILL);

        itsCustomCalendarView.setRtl(false);

        itsCustomCalendarView.setDisplayOtherMonthDays(false); //TODO: popraviti, ne sme da prelazi u sledeci red ako je trenutni ceo ispunjen
        itsCustomCalendarView.setOtherMonthDaysColor(getResources().getColor(R.color.red));

//        itsCustomCalendarView.setShouldScrollMonth(false);
//        itsCustomCalendarView.setShouldScrollMonthVerticaly(false);

        itsCustomCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        itsCustomCalendarView.setCalendarDatesTextColor(getResources().getColor(R.color.white));

        itsCustomCalendarView.setSelectedDateTextColor(Color.RED); //zavrsiti

        itsCustomCalendarView.shouldDisplayDividerForRows(false);
        itsCustomCalendarView.setRowsDividerColor(getResources().getColor(R.color.light_yellow));
        itsCustomCalendarView.setRowsDividerHeight(1);

        itsCustomCalendarView.show3DEffect(false);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener2 sensorEventListener2 = new SensorEventListener2() {
            @Override
            public void onFlushCompleted(Sensor sensor) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    itsCustomCalendarView.onSensorEvent(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener2, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        itsCustomCalendarView.showParallaxEffect(false);

//        itsCustomCalendarView.setTargetHeight(150);

        itsCustomCalendarView.setCurrentDate(calendar.getTime());
        monthName.setText(itsCustomCalendarView.getCurrentMonthName() + " " + itsCustomCalendarView.getYearStringForCurrentMonth());

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itsCustomCalendarView.scrollToLeft();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itsCustomCalendarView.scrollToRight();
            }
        });

        //Coloring for custom day column
//        itsCustomCalendarView.setWeekDaysForCustomizeItsColumn(new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.FRIDAY});
        itsCustomCalendarView.setWeekDaysForCustomizeItsColumn(new int[]{Calendar.MONDAY});
        itsCustomCalendarView.setCustomDayColumnColor(getResources().getColor(R.color.light_yellow));
        itsCustomCalendarView.shouldPaintCustomDayColumnColorForDayName(true);
        itsCustomCalendarView.shouldPaintCustomDayColumnColorForOtherMonthDays(false);
        itsCustomCalendarView.setShouldPaintCurrentDayForSelectedCustomizableDayColumn(false);

        //Events
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, 1);
//        itsCustomCalendarView.addEvent(new Event(Color.RED, cal.getTimeInMillis()));
//        cal.add(Calendar.DATE ,1);
//        itsCustomCalendarView.addEvent(new Event(Color.BLUE, cal.getTimeInMillis()));
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cal.add(Calendar.DATE, 1);
            events.add(new Event(Color.BLUE, cal.getTimeInMillis()));
        }
        itsCustomCalendarView.addEvents(events);

//        cal.add(Calendar.DATE, -1);
//        itsCustomCalendarView.removeEvents(cal.getTimeInMillis());


        /*Events listing*/
        List<String> eventi = new ArrayList<>();
//        for (int i = 0; i < itsCustomCalendarView.getEvents(cal.getTimeInMillis()).size(); i++) {
//            eventi.add("eventi: " + i);
//        }
//        System.out.println("eventi: " + eventi.size());

//        for (int i = 0; i < itsCustomCalendarView.getEventsForMonth(cal.getTimeInMillis()).size(); i++) {
//            eventi.add("eventi: " + i);
//        }
//        System.out.println("eventi: " + eventi.size());

//        Date date = new Date();
//        date = cal.getTime();
//        for (int i = 0; i < itsCustomCalendarView.getEventsForMonth(date).size(); i++) {
//            eventi.add("eventi: " + i);
//        }
//        System.out.println("eventi: " + eventi.size());
    }

    @Override
    public void onDayClick(Date dateClicked) {
        Toast.makeText(this, dateClicked.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        monthName.setText(itsCustomCalendarView.getMonthNameForSelectedDate(firstDayOfNewMonth) + " " + itsCustomCalendarView.getYearStringForSelectedDate(firstDayOfNewMonth));
    }
}
