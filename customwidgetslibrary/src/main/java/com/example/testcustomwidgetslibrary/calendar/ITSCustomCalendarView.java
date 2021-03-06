package com.example.testcustomwidgetslibrary.calendar;

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

import com.example.testcustomwidgetslibrary.calendar.event.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ITSCustomCalendarView extends View {
    private GestureDetectorCompat gestureDetector;
    private ITSCustomCalendarController itsCustomCalendarController;
    private boolean horizontalScrollEnabled = true;
    private boolean verticalScrollEnabled = false;

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
            } else if (verticalScrollEnabled) {
                if (Math.abs(distanceY) > 0) {
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
                Color.argb(255, 64, 64, 64), VelocityTracker.obtain(), new EventsOperations(Calendar.getInstance()));
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public ITSCustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        itsCustomCalendarController = new ITSCustomCalendarController(getContext(), Locale.getDefault(), TimeZone.getDefault(), attrs, new OverScroller(getContext()),
                new Paint(), new Rect(), Color.argb(255, 233, 84, 81), Color.argb(255, 219, 219, 219),
                Color.argb(255, 64, 64, 64), VelocityTracker.obtain(), new EventsOperations(Calendar.getInstance()));
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
    @Override
    public boolean canScrollVertically(int direction) {
        if (this.getVisibility() == GONE) {
            return false;
        }
        return this.verticalScrollEnabled;
    }

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

    public void setGridViewColor(int gridViewColor) {
        itsCustomCalendarController.setGridViewColor(gridViewColor);
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

    public void setShouldPaintWeekendDaysForOtherMonths(boolean shouldPaintWeekendDaysForOtherMonths) {
        itsCustomCalendarController.setShouldPaintWeekendDaysForOtherMonths(shouldPaintWeekendDaysForOtherMonths);
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


    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Current day column params>

    //Select week day to customize column for that day
    public void setWeekDaysForCustomizeItsColumn(int[] weekDays) {
        itsCustomCalendarController.setWeekDaysForCustomizeItsColumn(weekDays);
    }

    //Color for customDayColumn
    public void setCustomDayColumnColor(int customDayColumnColor) {
        itsCustomCalendarController.setCustomDayColumnColor(customDayColumnColor);
    }

    //True for paint otherMonth dates in customDayColumnColor, false for otherMonthDaysColor
    public void shouldPaintCustomDayColumnColorForOtherMonthDays(boolean shouldPaintCustomDayColumnForOtherMonthDays) {
        itsCustomCalendarController.shouldPaintCustomDayColumnColorForOtherMonthDays(shouldPaintCustomDayColumnForOtherMonthDays);
    }

    //True for paint custom column day name in customDayColumnColor, false for colorForWeekendDays
    public void shouldPaintCustomDayColumnColorForDayName(boolean shouldPaintCustomDayColumnColorForDayName) {
        itsCustomCalendarController.shouldPaintCustomDayColumnColorForDayName(shouldPaintCustomDayColumnColorForDayName);
    }

    //True for paint current day in selected customized column in customDayColumnColor, false for currentDateTextColor
    public void setShouldPaintCurrentDayForSelectedCustomizableDayColumn(boolean shouldPaintCurrentDayForSelectedCustomizableDayColumn) {
        itsCustomCalendarController.setShouldPaintCurrentDayForSelectedCustomizableDayColumn(shouldPaintCurrentDayForSelectedCustomizableDayColumn);
    }

/*

    //Select week day to customize column for that day
    public void setWeekDayForCustomizeItsColumn(int weekDay) {
        itsCustomCalendarController.setWeekDayForCustomizeItsColumn(weekDay);
    }

    //Color for customDayColumn
    public void setCustomDayColumnColor(int customDayColumnColor) {
        itsCustomCalendarController.setCustomDayColumnColor(customDayColumnColor);
    }

    //True for paint otherMonth dates in customDayColumnColor, false for otherMonthDaysColor
    public void shouldPaintCustomDayColumnColorForOtherMonthDays(boolean shouldPaintCustomDayColumnForOtherMonthDays) {
        itsCustomCalendarController.shouldPaintCustomDayColumnColorForOtherMonthDays(shouldPaintCustomDayColumnForOtherMonthDays);
    }

    //True for paint custom column day name in customDayColumnColor, false for colorForWeekendDays
    public void shouldPaintCustomDayColumnColorForDayName(boolean shouldPaintCustomDayColumnColorForDayName) {
        itsCustomCalendarController.shouldPaintCustomDayColumnColorForDayName(shouldPaintCustomDayColumnColorForDayName);
    }

    //True for paint current day in selected customized column in customDayColumnColor, false for currentDateTextColor
    public void setShouldPaintCurrentDayForSelectedCustomizableDayColumn(boolean shouldPaintCurrentDayForSelectedCustomizableDayColumn) {
        itsCustomCalendarController.setShouldPaintCurrentDayForSelectedCustomizableDayColumn(shouldPaintCurrentDayForSelectedCustomizableDayColumn);
    }
*/

    //</Current day column params>
    //--------------------------------------------------------------------------------


    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<EVENTS>

    public void setEventIndicatorShapeStyle(EventTypeShapes eventTypeShape) {
        itsCustomCalendarController.setEventIndicatorShapeStyle(eventTypeShape);
    }

    public void setEventIndicatorPaintStyle(Paint.Style paintStyle) {
        itsCustomCalendarController.setEventIndicatorPaintStyle(paintStyle);
    }

    public void setEventCalendarDatesTextColor(int eventCalendarDatesTextColor) {
        itsCustomCalendarController.setEventCalendarDatesTextColor(eventCalendarDatesTextColor);
    }

    public void setEventIndicatorShapePrimaryColor(int eventIndicatorShapePrimaryColor) {
        itsCustomCalendarController.setEventIndicatorShapePrimaryColor(eventIndicatorShapePrimaryColor);
    }

    public void setEventIndicatorShapeSecondaryColor(int eventIndicatorShapeSecondaryColor) {
        itsCustomCalendarController.setEventIndicatorShapeSecondaryColor(eventIndicatorShapeSecondaryColor);
    }

    public void addEvent(Event event) {
        addEvent(event, true);
    }

    public void addEvent(Event event, boolean shouldInvalidate) {
        itsCustomCalendarController.addEvent(event);
        if (shouldInvalidate) {
            invalidate();
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    public void addEvents(List<Event> events) {
        itsCustomCalendarController.addEvents(events);
        invalidate();
    }



    /**
     * Fetches the events for the date passed in
     * @param date
     * @return
     */
    public List<Event> getEvents(Date date){
        return itsCustomCalendarController.getCalendarEventsFor(date.getTime());
    }

    /**
     * Fetches the events for the epochMillis passed in
     * @param epochMillis
     * @return
     */
    public List<Event> getEvents(long epochMillis){
        return itsCustomCalendarController.getCalendarEventsFor(epochMillis);
    }

    /**
     * Fetches the events for the month of the epochMillis passed in and returns a sorted list of events
     * @param epochMillis
     * @return
     */
    public List<Event> getEventsForMonth(long epochMillis){
        return itsCustomCalendarController.getCalendarEventsForMonth(epochMillis);
    }

    /**
     * Fetches the events for the month of the date passed in and returns a sorted list of events
     * @param date
     * @return
     */
    public List<Event> getEventsForMonth(Date date){
        return itsCustomCalendarController.getCalendarEventsForMonth(date.getTime());
    }

    /**
     * Remove the event associated with the Date passed in
     * @param date
     */
    public void removeEvents(Date date){
        itsCustomCalendarController.removeEventsFor(date.getTime());
    }

    public void removeEvents(long epochMillis){
        itsCustomCalendarController.removeEventsFor(epochMillis);
    }

    /**
     * see {@link #removeEvent(Event, boolean)} when removing single events to control if calendar should redraw
     * or {@link #removeEvents(java.util.List)} (java.util.List)}  when removing multiple events
     * @param event
     */
    public void removeEvent(Event event){
        removeEvent(event, true);
    }

    /**
     * Removes an event from the calendar.
     * If removing multiple events see {@link #removeEvents(List)}
     *
     * @param event event to remove from the calendar
     * @param shouldInvalidate true if the view should invalidate
     */
    public void removeEvent(Event event, boolean shouldInvalidate){
        itsCustomCalendarController.removeEvent(event);
        if(shouldInvalidate){
            invalidate();
        }
    }

    /**
     * Removes multiple events from the calendar and invalidates the view once all events are added.
     */
    public void removeEvents(List<Event> events){
        itsCustomCalendarController.removeEvents(events);
        invalidate();
    }

    /**
     * Clears all Events from the calendar.
     */
    public void removeAllEvents() {
        itsCustomCalendarController.removeAllEvents();
        invalidate();
    }


    //</EVENTS>
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

    //Scrolls calendar to the right. Be carefull with RTL layouts (If RTL is true this scroll will show to the previous month.
    public void scrollToRight() {
        itsCustomCalendarController.scrollToRight();
        invalidate();
    }

    //Scrolls calendar to the left. Be carefull with RTL layouts (If RTL is true this scroll will show to the next month.
    public void scrollToLeft() {
        itsCustomCalendarController.scrollToLeft();
        invalidate();
    }

    public void setShouldScrollMonthHorizontaly(boolean enableHorizontalScroll) {
        this.horizontalScrollEnabled = enableHorizontalScroll;
        this.verticalScrollEnabled = !enableHorizontalScroll;
    }

    public void setShouldScrollMonthVerticaly(boolean enableVerticalScroll) {
        this.verticalScrollEnabled = enableVerticalScroll;
        this.horizontalScrollEnabled = !enableVerticalScroll;
    }

    public void setShouldScrollMonth(boolean shouldScrollMonth) {
        //if it's true than allow horizontal scrolling else disable both scrolling directions
        if (!shouldScrollMonth) {
            this.horizontalScrollEnabled = shouldScrollMonth;
            this.verticalScrollEnabled = shouldScrollMonth;
        } else {
            this.horizontalScrollEnabled = shouldScrollMonth;
            this.verticalScrollEnabled = !shouldScrollMonth;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (horizontalScrollEnabled || verticalScrollEnabled) {
            itsCustomCalendarController.onTouch(motionEvent);
            invalidate();
        }

        if ((motionEvent.getAction() == MotionEvent.ACTION_CANCEL || motionEvent.getAction() == MotionEvent.ACTION_UP) && (horizontalScrollEnabled || verticalScrollEnabled)) {
            //when onTouch finished, we need to allow again the parent to intercept touch events (scrolling)
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        //allow gesture detector onSingleTap and scroll events
        return gestureDetector.onTouchEvent(motionEvent);
    }


}
