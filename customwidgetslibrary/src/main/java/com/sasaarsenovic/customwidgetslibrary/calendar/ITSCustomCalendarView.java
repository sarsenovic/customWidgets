package com.sasaarsenovic.customwidgetslibrary.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ITSCustomCalendarView extends View {
    private GestureDetectorCompat gestureDetector;
    private ITSCustomCalendarController itsCustomCalendarController;
    private boolean horizontalScrollEnabled = true;

    public interface ITSCustomCalendarViewListener {
        void onDayClick(Date dateClicked);

        void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            itsCustomCalendarController.onSingleTapUp(e);
            invalidate();
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /*return super.onScroll(e1, e2, distanceX, distanceY);*/
            if (horizontalScrollEnabled) {
                if (Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true); //when child doesn't want this parent and its ancestors to intercept touch events with (Android docs)
                    itsCustomCalendarController.onScroll(e1, e2, distanceX, distanceY);
                    invalidate();
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /*return super.onFling(e1, e2, velocityX, velocityY);*/
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            /*return super.onDown(e);*/
            return true;
        }
    };

    public ITSCustomCalendarView(Context context) {
        super(context);
    }

    public ITSCustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        itsCustomCalendarController = new ITSCustomCalendarController(getContext(), Locale.getDefault(), TimeZone.getDefault(), attrs, new OverScroller(getContext()),
                new Paint(), new Rect(), Color.argb(255, 233, 84, 81), Color.argb(255, 219, 219, 219),
                Color.argb(255, 64, 64, 64), VelocityTracker.obtain());
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public ITSCustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        itsCustomCalendarController = new ITSCustomCalendarController(getContext(), Locale.getDefault(), TimeZone.getDefault(), attrs, new OverScroller(getContext()),
                new Paint(), new Rect(), Color.argb(255, 233, 84, 81), Color.argb(255, 219, 219, 219),
                Color.argb(255, 64, 64, 64), VelocityTracker.obtain());
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > 0 && height > 0) {
            itsCustomCalendarController.onMeasure(width, height, getPaddingRight(), getPaddingLeft());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        itsCustomCalendarController.onDraw(canvas);
    }

    /*
    Android docs:
    Called by a parent to request that a child update its values for mScrollX and mScrollY if necessary.
    This will typically be done if the child is animating a scroll using a Scroller object.
    */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (itsCustomCalendarController.computeScroll()) {
            invalidate();
        }
    }

    /*
    Android docs:
    Check if this view can be scrolled horizontally in a certain direction.
    */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.getVisibility() == GONE) {
            return false;
        }

        return this.horizontalScrollEnabled;
    }

    /*
    Android docs:
    Check if this view can be scrolled vertically in a certain direction.
    */
    /*@Override
    public boolean canScrollVertically(int direction) {
        if (this.getVisibility() == GONE) {
            return false;
        }
        return false;
    }*/

    //Use a custom locale for calendar. View is going to reinitialise
    public void setLocale(TimeZone timeZone, Locale locale) {
        itsCustomCalendarController.setLocale(timeZone, locale);
        invalidate();
    }

    /*
    Compact calendar will use the locale to determine the abbreviation to use as the day column names.
    The default is to use the default locale and to abbreviate the day names to one character.
    Setting this to true will displace the short weekday string provided by java.
     */
    public void setUsingThreeLetterForWeek(boolean useThreeLetter) {
        itsCustomCalendarController.setUsingThreeLetterForWeek(useThreeLetter);
        invalidate();
    }

    /*
    Sets the name for each day of the week. No attempt is made to adjust width or text size based on the length of each day name.
    Works best with 3-4 characters for each day.
     */
    public void setDayColumnNames(String[] dayColumnNames) {
        itsCustomCalendarController.setDayColumnNames(dayColumnNames);
    }

    public void setFirstDayOfWeek(int dayOfWeek) {
        itsCustomCalendarController.setFirstDayOfWeek(dayOfWeek);
        invalidate();
    }

    public void setShowGridView(boolean showGridView) {
        itsCustomCalendarController.setShowGridView(showGridView);
    }

    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Colors section>

    //Background color type
    public void setCalendarBackgroundColor(int backgroundColor) {
        itsCustomCalendarController.setCalendarBackgroundColor(backgroundColor);
        invalidate();
    }

    //Background color type
    public void setCurrentDayBackroundColor(int currentDayBackroundColor) {
        itsCustomCalendarController.setCurrentDayBackroundColor(currentDayBackroundColor);
        invalidate();
    }

    //Background color type
    /*
    public void setSelectedDayBackgroundColor(int selectedDayBackgroundColor) {
        itsCustomCalendarController.setSelectedDayBackgroundColor(selectedDayBackgroundColor);
        invalidate();
    }
    */

    //Text color type
    public void setCalendarDatesTextColor(int calendarDatesTextColor) {
        itsCustomCalendarController.setCalendarDatesTextColor(calendarDatesTextColor);
    }

    //Text color type
    public void setCalendarWeekDaysTextColor(int calendarWeekDaysTextColor) {
        itsCustomCalendarController.setCalendarWeekDaysTextColor(calendarWeekDaysTextColor);
    }

    //Text color type
    public void setSelectedDateTextColor(int selectedDateTextColor) {
        itsCustomCalendarController.setSelectedDateTextColor(selectedDateTextColor);
    }

    //Text color type
    public void setCurrentDateTextColor(int currentDateTextColor) {
        itsCustomCalendarController.setCurrentDateTextColor(currentDateTextColor);
    }

    public void setOtherMonthDaysColor(int otherMonthDaysColor) {
        itsCustomCalendarController.setOtherMonthDaysColor(otherMonthDaysColor);
    }

    public void setColorForWeekendDays(int colorForWeekendDays) {
        itsCustomCalendarController.setColorForWeekendDays(colorForWeekendDays);
    }

    public void shouldPaintWeekendDaysForOtherMonths(boolean shouldPaintWeekendDaysForOtherMonths) {
        itsCustomCalendarController.shouldPaintWeekendDaysForOtherMonths(shouldPaintWeekendDaysForOtherMonths);
    }

    //Indicator color type
    /*
    public void setSelectedDateCircleIndicatorColor(int selectedDateCircleIndicatorColor) {
        itsCustomCalendarController.setSelectedDateCircleIndicatorColor(selectedDateCircleIndicatorColor);
    }
    */

    //Indicator color type
    /*
    public void setCurrentDateCircleIndicatorColor(int currentDateCircleIndicatorColor) {
        itsCustomCalendarController.setCurrentDateCircleIndicatorColor(currentDateCircleIndicatorColor);
    }
    */

    //</Colors section>
    //--------------------------------------------------------------------------------

    /*
    public int getDayHeight() {
        return itsCustomCalendarController.getDayHeight();
    }
    */

    public void setListener(ITSCustomCalendarViewListener callback) {
        itsCustomCalendarController.setListener(callback);
    }

    /*
    public Date getFirstDayOfCurrentMonth() {
        return itsCustomCalendarController.getFirstDayOfCurrentMonth();
    }
    */

    /*
    public void setShouldDrawIndicatorsBelowSelectedDays(boolean shouldDrawIndicatorsBelowSelectedDays) {
        itsCustomCalendarController.setShouldDrawIndicatorsBelowSelectedDays(shouldDrawIndicatorsBelowSelectedDays);
    }
    */

    public void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader) {
        itsCustomCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader);
    }

    public void setCurrentDate(Date date) {
        itsCustomCalendarController.setCurrentDate(date);
        invalidate();
    }

    public String getCurrentMonthName() {
        return itsCustomCalendarController.getCurrentMonthName();
    }

    public String getMonthNameForSelectedDate(Date date) {
        return itsCustomCalendarController.getMonthNameForSelectedDate(date);
    }

    public String getYearStringForCurrentMonth() {
        return itsCustomCalendarController.getYearStringForCurrentMonth();
    }

    public String getYearStringForSelectedDate(Date date) {
        return itsCustomCalendarController.getYearStringForSelectedDate(date);
    }

    /*
    public int getWeekNumberForCurrentMonth() {
        return itsCustomCalendarController.getWeekNumberForCurrentMonth();
    }
    */

    public void setRtl(boolean isRtl) {
        itsCustomCalendarController.setRtl(isRtl);
    }

    public void setShouldSelectFirstDayOfMonthOnScroll(boolean shouldSelectFirstDayOfMonthOnScroll) {
        itsCustomCalendarController.setShouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDayOfMonthOnScroll);
    }

    public void setSelectedDateIndicatorShape(IndicatorShapes indicatorShape) {
        itsCustomCalendarController.setSelectedDateIndicatorShape(indicatorShape);
        invalidate();
    }


    //STROKE, FILL, STROKE_AND_FILL
    public void setSelectedDateIndicatorStyle(final int selectedDateIndicatorStyle) {
        itsCustomCalendarController.setSelectedDateIndicatorStyle(selectedDateIndicatorStyle);
        invalidate();
    }

    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Custom days color params>

    //ubacivanje datuma ili opsega datuma za iscrtavanje "po narudzbini"
    public void setCustomDaysForCustomDrawing(Date[] dates) {
        itsCustomCalendarController.setCustomDaysForCustomDrawing(dates);
        invalidate();
    }

    //STROKE, FILL, FILL_AND_STROKE
    public void setCustomDaysPaintStyle(Paint.Style paintStyle) {
        itsCustomCalendarController.setCustomDaysPaintStyle(paintStyle);
        invalidate();
    }

    //postavljanje boje teksta za izabrane datume
    public void setCustomDaysTextColor(int customDaysTextColor) {
        itsCustomCalendarController.setCustomDaysTextColor(customDaysTextColor);
    }

    //</Custom day color params>
    //--------------------------------------------------------------------------------


    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Custom days indicator shape params>

    //ubacivanje datuma ili opsega datuma za iscrtavanje oblika (obelezavanje tih datuma) "po narudzbini"
    public void setCustomDaysForCustomDrawingShapes(Date[] dates) {
        itsCustomCalendarController.setCustomDaysForCustomDrawingShapes(dates);
        invalidate();
    }

    //izbor oblika koji ce se nalaziti iza postavljenih datuma
    public void setCustomDaysIndicatorShape(IndicatorShapes indicatorShape) {
        itsCustomCalendarController.setCustomDaysIndicatorShape(indicatorShape);
        invalidate();
    }

    //STROKE, FILL, FILL_AND_STROKE
    public void setCustomDaysIndicatorShapePaintStyle(Paint.Style customDayIndicatorShapePaintStyle) {
        itsCustomCalendarController.setCustomDaysIndicatorShapePaintStyle(customDayIndicatorShapePaintStyle);
        invalidate();
    }

    public void setCustomDayIndicatorShapeColor(int customDayIndicatorShapeColor) {
        itsCustomCalendarController.setCustomDayIndicatorShapeColor(customDayIndicatorShapeColor);
    }

    public void shouldShowIndicatorShapeForCustomDay(boolean shouldShowIndicatorShapeForCustomDay) {
        itsCustomCalendarController.shouldShowIndicatorShapeForCustomDay(shouldShowIndicatorShapeForCustomDay);
    }

    //</Custom day indicator shape params>
    //--------------------------------------------------------------------------------


    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Current day indicator shape params>

    public void setCurrentDayIndicatorShape(IndicatorShapes indicatorShape) {
        itsCustomCalendarController.setCurrentDayIndicatorShape(indicatorShape);
//        invalidate();
    }

    /*STROKE, FILL, STROKE_AND_FILL*/
    public void setCurrentDayIndicatorStyle(Paint.Style currentDayIndicatorStyle) {
        itsCustomCalendarController.setCurrentDayIndicatorStyle(currentDayIndicatorStyle);
//        invalidate();
    }

    public void setCurrentDayIndicatorColor(int currentDayIndicatorColor) {
        itsCustomCalendarController.setCurrentDayIndicatorColor(currentDayIndicatorColor);
    }

    public void shouldShowIndicatorForCurrentDay(boolean shouldShowIndicatorForCurrentDay) {
        itsCustomCalendarController.shouldShowIndicatorForCurrentDay(shouldShowIndicatorForCurrentDay);
    }

    //</Current day indicator shape params>
    //--------------------------------------------------------------------------------

    public void setDisplayOtherMonthDays(boolean displayOtherMonthDays) {
        itsCustomCalendarController.setDisplayOtherMonthDays(displayOtherMonthDays);
        invalidate();
    }

    /*public void setEventIndicatorStyle(final int eventIndicatorStyle){
        itsCustomCalendarController.setEventIndicatorStyle(eventIndicatorStyle);
        invalidate();
    }*/

    public void setTargetHeight(int targetHeightInDp) {
        itsCustomCalendarController.setTargetHeight(targetHeightInDp);
        checkTargetHeight();
    }

    private void checkTargetHeight() {
        if (itsCustomCalendarController.getTargetHeight() <= 0) {
            throw new IllegalStateException("Target height must be set in xml properties in order to expand/collapse ITSCustomCalendar");
        }
    }

    public void shouldDrawLineDividerUnderWeekDaysHeader(boolean shouldDrawLineDividerUnderWeekDaysHeader) {
        itsCustomCalendarController.shouldDrawLineDividerUnderWeekDaysHeader(shouldDrawLineDividerUnderWeekDaysHeader);
    }

    public void setLineDividerUnderWeekDaysHeaderColor(int color) {
        itsCustomCalendarController.setLineDividerUnderWeekDaysHeaderColor(color);
    }

    public void setLineDividerUnderWeekDaysHeaderHeight(int heightInDp) {
        if (itsCustomCalendarController.isShouldDrawLineDividerUnderWeekDaysHeader()) {
            itsCustomCalendarController.setLineDividerUnderWeekDaysHeaderHeight(heightInDp);
        }
    }

    public void shouldDisplayDividerForRows(boolean shouldDisplayDividerForRows) {
        itsCustomCalendarController.shouldDisplayDividerForRows(shouldDisplayDividerForRows);
    }

    public void setRowsDividerColor(int rowsDividerColor) {
        itsCustomCalendarController.setRowsDividerColor(rowsDividerColor);
    }

    public void setRowsDividerHeight(int rowsDividerHeightInDp) {
        itsCustomCalendarController.setRowsDividerHeight(rowsDividerHeightInDp);
    }

    public void show3DEffect(boolean show3DEffect) {
        itsCustomCalendarController.show3DEffect(show3DEffect);
    }

    public void showParallaxEffect(boolean showParallaxEffect) {
        itsCustomCalendarController.showParallaxEffect(showParallaxEffect);
    }

    public void onSensorEvent(SensorEvent sensorEvent) {
        itsCustomCalendarController.onSensorEvent(sensorEvent);
        invalidate();
    }

    //    Scrolls calendar to the right. Be carefull with RTL layouts (If RTL is true this scroll will show to the previous month.
    public void scrollToRight() {
        itsCustomCalendarController.scrollToRight();
        invalidate();
    }

    //    Scrolls calendar to the left. Be carefull with RTL layouts (If RTL is true this scroll will show to the next month.
    public void scrollToLeft() {
        itsCustomCalendarController.scrollToLeft();
        invalidate();
    }

    public void shouldScrollMonth(boolean enableHorizontalScroll) {
        this.horizontalScrollEnabled = enableHorizontalScroll;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (horizontalScrollEnabled) {
            itsCustomCalendarController.onTouch(motionEvent);
            invalidate();
        }

        if ((motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP) && horizontalScrollEnabled) {
            //when onTouch finished, we need to allow again the parent to intercept touch events (scrolling)
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        //allow gesture detector onSingleTap and scroll events
        return gestureDetector.onTouchEvent(motionEvent);
    }

}
