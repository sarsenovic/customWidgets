package com.example.customwidgetslibrary.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import android.widget.Toast;

import com.example.customwidgetslibrary.Calculations;
import com.example.customwidgetslibrary.LoaderForFonts;
import com.example.customwidgetslibrary.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class ITSCustomCalendarController {
    private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;
    private static final int DAYS_IN_WEEK = 7;
    private static final float SNAP_VELOCITY_DIP_PER_SECOND = 400;
    private static final int LAST_FLING_THRESHOLD_MILLIS = 300;

    private ITSCustomCalendarView.ITSCustomCalendarViewListener listener;

    private int calendarWeekDaysTextColor;
    private int calendarDatesTextColor;
    private int calendarBackgroundColor;
    private int currentDayBackgroundColor;

    private int selectedDateTextColor;
    private int selectedDateCircleIndicatorColor;

    private int currentDateTextColor;
    private int currentDateCircleIndicatorColor;

    private int width;
    private int height;
    private boolean isRtl = false;

    private int paddingWidth = 40;
    private int paddingHeight = 40;
    private int paddingRight;
    private int paddingLeft;
    private int widthPerDay;
    private int targetHeight;
    private int heightPerDay;
    private float growFactor = 0f;
    private int monthsScrolledSoFar;
    private int maximumVelocity;
    private boolean shouldDrawDaysHeader = true;
    private boolean displayOtherMonthDays = false;
    private Direction direction = Direction.NONE;
    private VelocityTracker velocityTracker = null;
    private PointF accumulatedScrollOffset = new PointF();
    private float distanceX;
    private Calendar calendarWithFirstDayOfMonth;
    private Date currentDate = new Date();
    private int firstDayOfWeekToDraw = Calendar.MONDAY;
    private String[] dayColumnNames;
    private float screenDensity = 1;
    private float growfactorIndicator;
    private int densityAdjustedSnapVelocity;
    private float multiDayIndicatorStrokeWidth;
    private float xIndicatorOffset;
    private float smallIndicatorRadius;
    private int textHeight;
    private int textWidth;
    private boolean useThreeLetterAbbreviation = false;
    private int textSize = 20;
    private int distanceThresholdForAutoScroll;
    private boolean shouldDrawLineDividerUnderWeekDaysHeader = false;
    private float lineDividerUnderWeekDaysHeaderHeight = 1;
    private int lineDividerUnderWeekDaysHeaderColor = Color.BLACK;
    private boolean isSmoothScrolling;
    private boolean isScrolling;
    private long lastAutoScrollFromFling;

    private OverScroller overScroller;
    private Rect textSizeRect;
    private Locale locale;
    private TimeZone timeZone;
    private Typeface typeface;
    private Calendar currentCalendar;
    private Calendar todayCalendar;
    private Calendar previousMonthCalendar;
    private Paint dayPaint = new Paint();
    private Context context;

    public ITSCustomCalendarController(Context context, Locale locale, TimeZone timeZone, AttributeSet attrs, OverScroller overScroller, Paint dayPaint,
                                       Rect textSizeRect, int currentDayBackgroundColor, int currentSelectedDayBackgroundColor, int calendarTextColor,
                                       VelocityTracker velocityTracker) {
        this.overScroller = overScroller;
        this.locale = locale;
        this.timeZone = timeZone;
        this.velocityTracker = velocityTracker;
        this.dayPaint = dayPaint;
        this.textSizeRect = textSizeRect;
        this.calendarDatesTextColor = calendarTextColor;
        this.context = context;
        loadAttrs(attrs, context);
        init(context);
    }

    private void loadAttrs(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ITSCustomCalendarView, 0, 0);
            typeface = LoaderForFonts.getTypeface(context, attrs);

            try {
                calendarWeekDaysTextColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_calendarWeekDaysTextColor, calendarWeekDaysTextColor);
                calendarDatesTextColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_calendarDatesTextColor, calendarDatesTextColor);
                calendarBackgroundColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_calendarBackgroundColor, calendarBackgroundColor);
                currentDayBackgroundColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_currentDayBackgroundColor, currentDayBackgroundColor);

                selectedDateTextColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_selectedDateTextColor, selectedDateTextColor);
                selectedDateCircleIndicatorColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_selectedDateCircleIndicatorColor, selectedDateCircleIndicatorColor);

                currentDateTextColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_currentDateTextColor, currentDateTextColor);
                currentDateCircleIndicatorColor = typedArray.getColor(R.styleable.ITSCustomCalendarView_currentDateCircleIndicatorColor, currentDateCircleIndicatorColor);

                textSize = typedArray.getDimensionPixelSize(R.styleable.ITSCustomCalendarView_calendarTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.ITSCustomCalendarView_targetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(Context context) {
        currentCalendar = Calendar.getInstance(timeZone, locale);
        todayCalendar = Calendar.getInstance(timeZone, locale);
        calendarWithFirstDayOfMonth = Calendar.getInstance(timeZone, locale);
        previousMonthCalendar = Calendar.getInstance(timeZone, locale);

        currentCalendar.setMinimalDaysInFirstWeek(1);
        todayCalendar.setMinimalDaysInFirstWeek(1);
        calendarWithFirstDayOfMonth.setMinimalDaysInFirstWeek(1);
        previousMonthCalendar.setMinimalDaysInFirstWeek(1);

        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        if (typeface != null) {
            dayPaint.setTypeface(typeface);
        } else {
            dayPaint.setTypeface(Typeface.SANS_SERIF);
        }
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calendarDatesTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), textSizeRect);
        textHeight = textSizeRect.height() * 3;
        textWidth = textSizeRect.width() * 2;

        todayCalendar.setTime(new Date());
        currentCalendar.setTime(currentDate);

        setCalendarToMidnight(todayCalendar);
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        setUsingThreeLetterForWeek(false);

        initScreenDensityRelatedValues(context);

        xIndicatorOffset = 3.5f * screenDensity;
        smallIndicatorRadius = 2.5f * screenDensity;
        growFactor = Integer.MAX_VALUE;
    }

    void onDraw(Canvas canvas) {
        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        calculateXPositionOfset();

        drawCalendarBackground(canvas);
        drawScrollableCalendar(canvas);
    }

    private void setCalendarToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void initScreenDensityRelatedValues(Context context) {
        if (context != null) {
            screenDensity = context.getResources().getDisplayMetrics().density;
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);

            densityAdjustedSnapVelocity = (int) (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND);
            maximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            multiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
        }
    }

    private void calculateXPositionOfset() {
        if (direction == Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX;
        }
    }

    private void drawCalendarBackground(Canvas canvas) {
        dayPaint.setColor(calendarBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL); //TODO: Do STROKE style
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calendarDatesTextColor);
    }

    private void drawScrollableCalendar(Canvas canvas) {
        if (isRtl) {
            drawNextMonth(canvas, -1);
            drawCurrentMonth(canvas);
            drawPreviousMonth(canvas, 1);
        } else {
            drawPreviousMonth(canvas, -1);
            drawCurrentMonth(canvas);
            drawNextMonth(canvas, 1);
        }
    }

    private void drawPreviousMonth(Canvas canvas, int offset) {
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, offset);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void drawCurrentMonth(Canvas canvas) {
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, monthsScrolledSoFar(), 0);
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar);
    }

    private int monthsScrolledSoFar() {
        return isRtl ? monthsScrolledSoFar : -monthsScrolledSoFar;
    }

    private void drawNextMonth(Canvas canvas, int offset) {
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, offset);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void setCalendarToFirstDayOfMonth(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        setMonthOffset(calendarWithFirstDayOfMonth, currentDate, scrollOffset, monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
    }

    private void setMonthOffset(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        calendarWithFirstDayOfMonth.setTime(currentDate);
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, scrollOffset + monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MINUTE, 0);
        calendarWithFirstDayOfMonth.set(Calendar.SECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MILLISECOND, 0);
    }

    int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeekToDraw;
        dayOfWeek = dayOfWeek < 0 ? 7 + dayOfWeek : dayOfWeek;
        return dayOfWeek;
    }

    private void drawMonth(Canvas canvas, Calendar monthForDrawCal, int offset) {
        int firstDayOfMonth = getDayOfWeek(monthForDrawCal);

        boolean isSameMonthAsToday = monthForDrawCal.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH);
        boolean isSameYearAsToday = monthForDrawCal.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR);
        boolean isSameMonthAsCurrentCalendar = monthForDrawCal.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && monthForDrawCal.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
        int todayDayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
        int maxMonthDay = monthForDrawCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        previousMonthCalendar.setTimeInMillis(monthForDrawCal.getTimeInMillis());
        previousMonthCalendar.add(Calendar.MONTH, -1);
        int maxPreviousMonthDay = previousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int dayColumn = 0, columnDirection = isRtl ? 6 : 0, dayRow = 0; dayColumn <= 6; dayRow++) {
            if (dayRow == 7) {
                if (isRtl) {
                    columnDirection--;
                } else {
                    columnDirection++;
                }
                dayRow = 0;
//                if (dayColumn <= 6) {
                dayColumn++;
//                }
            }
            if (dayColumn == dayColumnNames.length) {
                break;
            }

            float xPosition = widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
            float yPosition = heightPerDay * dayRow + paddingHeight;

            if (dayRow == 0) {
                if (shouldDrawDaysHeader) {
                    dayPaint.setColor(calendarWeekDaysTextColor);
                    if (typeface != null) {
                        dayPaint.setTypeface(typeface);
                    } else {
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                    dayPaint.setStyle(Paint.Style.FILL);
                    canvas.drawText(dayColumnNames[columnDirection], xPosition, paddingHeight, dayPaint);
                    dayPaint.setTypeface(Typeface.DEFAULT); //Reset typeface

                    if (shouldDrawLineDividerUnderWeekDaysHeader) {
                        dayPaint.setColor(lineDividerUnderWeekDaysHeaderColor);
//                        canvas.drawLine(0, paddingHeight + 20 + lineDividerUnderWeekDaysHeaderHeight, width, paddingHeight + 20 + lineDividerUnderWeekDaysHeaderHeight, dayPaint);
                        canvas.drawRect(new RectF(0, paddingHeight + 20, width, paddingHeight + 20 + lineDividerUnderWeekDaysHeaderHeight), dayPaint);
//                        shouldDrawLineDividerUnderWeekDaysHeader = false;
                        dayPaint.setColor(calendarWeekDaysTextColor); //Reset day paint
                    }
                }
            } else {
                int day = ((dayRow - 1) * 7 + columnDirection + 1) - firstDayOfMonth;
                if (currentCalendar.get(Calendar.DAY_OF_MONTH) == day && isSameMonthAsCurrentCalendar) {
                    //obelezi ovaj datum
                } else if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day) {

                }
                if (day <= 0) {
//                    previous month days
                    if (displayOtherMonthDays) {
                        dayPaint.setStyle(Paint.Style.FILL);
//                        dayPaint.setColor(otherMonthDaysColor);
                        dayPaint.setColor(Color.GRAY);
                        if (typeface != null) {
                            dayPaint.setTypeface(typeface);
                        } else {
                            dayPaint.setTypeface(Typeface.DEFAULT);
                        }
                        canvas.drawText(String.valueOf(maxPreviousMonthDay + day), xPosition, yPosition, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else if (day > maxMonthDay) {
//                    next month days
                    if (displayOtherMonthDays) {
                        dayPaint.setStyle(Paint.Style.FILL);
//                        dayPaint.setColor(otherMonthDaysColor);
                        dayPaint.setColor(Color.GRAY);
                        if (typeface != null) {
                            dayPaint.setTypeface(typeface);
                        } else {
                            dayPaint.setTypeface(Typeface.DEFAULT);
                        }
                        canvas.drawText(String.valueOf(day - maxMonthDay), xPosition, yPosition, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {
                    dayPaint.setStyle(Paint.Style.FILL);
                    if (dayRow == 2) //testing line
                        dayPaint.setColor(Color.WHITE);
                    else
                        dayPaint.setColor(calendarDatesTextColor);
                    if (typeface != null) {
                        dayPaint.setTypeface(typeface);
                    } else {
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                    canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                    dayPaint.setTypeface(Typeface.DEFAULT); //Reset typeface
                }
            }

        }
    }

    private int computeVelocity() {
        velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity);
        return (int) velocityTracker.getXVelocity();
    }

    void onSingleTapUp(MotionEvent e) {

    }

    void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
        widthPerDay = (width) / DAYS_IN_WEEK;
        heightPerDay = targetHeight > 0 ? targetHeight / 7 : height / 7;
        this.width = width;
        this.distanceThresholdForAutoScroll = (int) (width * 0.50);
        this.height = height;
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;

//        //makes easier to find radius
//        bigCircleIndicatorRadius = getInterpolatedBigCircleIndicator();
//
//        // scale the selected day indicators slightly so that event indicators can be drawn below
//        bigCircleIndicatorRadius = shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == CompactCalendarView.SMALL_INDICATOR ? bigCircleIndicatorRadius * 0.85f : bigCircleIndicatorRadius;
    }

    void setLocale(TimeZone timeZone, Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        if (timeZone == null) {
            throw new IllegalArgumentException("TimeZone cannot be null.");
        }
        this.locale = locale;
        this.timeZone = timeZone;
        // passing null will not re-init density related values - and that's ok
        init(null);
    }


    void setUsingThreeLetterForWeek(boolean useThreeLetter) {
        //setUseWeekDayAbbreviation
        this.useThreeLetterAbbreviation = useThreeLetter;
        this.dayColumnNames = WeekUtils.getWeekdayNames(locale, firstDayOfWeekToDraw, this.useThreeLetterAbbreviation);
    }

    void setCalendarBackgroundColor(int backgroundColor) {
        this.calendarBackgroundColor = backgroundColor;
    }

    void setDayColumnNames(String[] dayColumnNames) {
        if (dayColumnNames == null || dayColumnNames.length != 7) {
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }

    void setFirstDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day must be an int between 1 and 7 or DAY_OF_WEEK from Java Calendar class. For more information please see Calendar.DAY_OF_WEEK.");
        }
        this.firstDayOfWeekToDraw = dayOfWeek;
        setUsingThreeLetterForWeek(useThreeLetterAbbreviation);
        calendarWithFirstDayOfMonth.setFirstDayOfWeek(dayOfWeek);
        todayCalendar.setFirstDayOfWeek(dayOfWeek);
        currentCalendar.setFirstDayOfWeek(dayOfWeek);
        previousMonthCalendar.setFirstDayOfWeek(dayOfWeek);
    }

    boolean computeScroll() {
        if (overScroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = overScroller.getCurrX();
            return true;
        }
        return false;
    }

    boolean onTouch(MotionEvent motionEvent) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(motionEvent);

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            isSmoothScrolling = false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            velocityTracker.addMovement(motionEvent);
            velocityTracker.computeCurrentVelocity(500);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            handleHorizontalScrolling();
            velocityTracker.recycle();
            velocityTracker.clear();
            velocityTracker = null;
            isScrolling = false;
        }
        return false;
    }

    private void handleHorizontalScrolling() {
        int velocityX = computeVelocity();
        handleSmoothScrolling(velocityX);

        direction = Direction.NONE;
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, monthsScrolledSoFar(), 0);

        if (calendarWithFirstDayOfMonth.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)) {
            setCalendarToFirstDayOfMonth(currentCalendar, currentDate, monthsScrolledSoFar(), 0);
        }
    }

    private void handleSmoothScrolling(int velocityX) {
        int distanceScrolled = (int) (accumulatedScrollOffset.x - (width * monthsScrolledSoFar));
        boolean isEnoughTimeElapsedSinceLastSmoothScroll = System.currentTimeMillis() - lastAutoScrollFromFling > LAST_FLING_THRESHOLD_MILLIS;
        if (velocityX > densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollPreviousMonth();
        } else if (velocityX < -densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollNextMonth();
        } else if (isScrolling && distanceScrolled > distanceThresholdForAutoScroll) {
            scrollPreviousMonth();
        } else if (isScrolling && distanceScrolled < -distanceThresholdForAutoScroll) {
            scrollNextMonth();
        } else {
            isSmoothScrolling = false;
            snapBackScroller();
        }
    }

    private void scrollNextMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis();
        monthsScrolledSoFar = monthsScrolledSoFar - 1;
        performScroll();
        isSmoothScrolling = true;
        performMonthScrollCallback();
    }

    private void scrollPreviousMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis();
        monthsScrolledSoFar = monthsScrolledSoFar + 1;
        performScroll();
        isSmoothScrolling = true;
        performMonthScrollCallback();
    }

    private void performScroll() {
        int targetScroll = (monthsScrolledSoFar ) * width;
        float remainingScrollAfterFingerLifted = targetScroll - accumulatedScrollOffset.x;
//        overScroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) (remainingScrollAfterFingerLifted), 0,
//                (int) (Math.abs((int) (remainingScrollAfterFingerLifted)) / (float) width * ANIMATION_SCREEN_SET_DURATION_MILLIS));
        overScroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) (remainingScrollAfterFingerLifted), 0,
                (int) (Math.abs((int) (remainingScrollAfterFingerLifted)) / (float) width));
    }

    private void performMonthScrollCallback() {
        if (listener != null) {
            listener.onMonthScroll(getFirstDayOfCurrentMonth());
        }
    }

    Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, monthsScrolledSoFar());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setCalendarToMidnight(calendar);
        return calendar.getTime();
    }

    private void snapBackScroller() {
        float remainingScrollAfterFingerLifted1 = (accumulatedScrollOffset.x - (monthsScrolledSoFar * width));
        overScroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) -remainingScrollAfterFingerLifted1, 0);
    }

    void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    int getTargetHeight() {
        return targetHeight;
    }

    void setCurrentDayBackroundColor(int currentDayBackroundColor) {
        this.currentDayBackgroundColor = currentDayBackroundColor;
    }

    private void drawRowIndicator(Canvas canvas, int width, int height) {

    }

    void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader) {
        this.shouldDrawDaysHeader = shouldDrawDaysHeader;
    }

    void shouldDrawLineDividerUnderWeekDaysHeader(boolean shouldDrawLineDividerUnderWeekDaysHeader) {
        this.shouldDrawLineDividerUnderWeekDaysHeader = shouldDrawLineDividerUnderWeekDaysHeader;
    }

    boolean isShouldDrawLineDividerUnderWeekDaysHeader() {
        return shouldDrawLineDividerUnderWeekDaysHeader;
    }

    void setLineDividerUnderWeekDaysHeaderHeight(int heightInDp) {
        this.lineDividerUnderWeekDaysHeaderHeight = Calculations.getPxFromDp(context, heightInDp);
    }

    void setLineDividerUnderWeekDaysHeaderColor(int color) {
        this.lineDividerUnderWeekDaysHeaderColor = color;
    }

    void setRtl(boolean isRtl) {
        this.isRtl = isRtl;
    }

    void setDisplayOtherMonthDays(boolean displayOtherMonthDays) {
        this.displayOtherMonthDays = displayOtherMonthDays;
    }

    void setListener(ITSCustomCalendarView.ITSCustomCalendarViewListener callback) {
        this.listener = callback;
    }
}