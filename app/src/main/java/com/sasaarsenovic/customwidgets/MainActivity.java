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

import java.util.Calendar;
import java.util.Date;
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
        itsCustomCalendarView.setShowGridView(true);
//        itsCustomCalendarView.setGridViewColor(getResources().getColor(R.color.yellow));

        itsCustomCalendarView.setShouldDrawDaysHeader(true);
        itsCustomCalendarView.setUsingThreeLetterForWeek(false);
        itsCustomCalendarView.setCalendarWeekDaysTextColor(getResources().getColor(R.color.midnight_blue));
        itsCustomCalendarView.setShouldPaintWeekendDaysForOtherMonths(false);
        itsCustomCalendarView.shouldDrawLineDividerUnderWeekDaysHeader(false);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderHeight(1);
        itsCustomCalendarView.setLineDividerUnderWeekDaysHeaderColor(getResources().getColor(R.color.white));

        itsCustomCalendarView.setColorForWeekendDays(getResources().getColor(R.color.yellow));

        itsCustomCalendarView.setRtl(false);

        itsCustomCalendarView.setDisplayOtherMonthDays(false); //TODO: popraviti, ne sme da prelazi u sledeci red ako je trenutni ceo ispunjen
        itsCustomCalendarView.setOtherMonthDaysColor(getResources().getColor(R.color.red));

//        itsCustomCalendarView.setShouldScrollMonth(false);
//        itsCustomCalendarView.setShouldScrollMonthVerticaly(false);

        itsCustomCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        itsCustomCalendarView.setCalendarDatesTextColor(getResources().getColor(R.color.white));

        itsCustomCalendarView.setCurrentDateTextColor(getResources().getColor(R.color.red_light));
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

//        itsCustomCalendarView.setTargetHeight(350);

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

        itsCustomCalendarView.shouldShowIndicatorForCurrentDay(true);
        itsCustomCalendarView.setCurrentDayIndicatorShape(IndicatorShapes.CIRCLE);
        itsCustomCalendarView.setCurrentDayIndicatorColor(getResources().getColor(R.color.white));
        itsCustomCalendarView.setCurrentDayIndicatorStyle(Paint.Style.FILL);

        //Coloring for custom day column
        itsCustomCalendarView.setWeekDayForCustomizeItsColumn(Calendar.WEDNESDAY);
        itsCustomCalendarView.setCustomDayColumnColor(getResources().getColor(R.color.orange));
        itsCustomCalendarView.shouldPaintCustomDayColumnColorForDayName(true);
        itsCustomCalendarView.shouldPaintCustomDayColumnColorForOtherMonthDays(true);
        itsCustomCalendarView.setShouldPaintCurrentDayForSelectedCustomizableDayColumn(true);
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
