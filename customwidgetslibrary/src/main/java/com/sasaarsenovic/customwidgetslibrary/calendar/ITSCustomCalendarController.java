package com.sasaarsenovic.customwidgetslibrary.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.SensorEvent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.sasaarsenovic.customwidgetslibrary.Calculations;
import com.sasaarsenovic.customwidgetslibrary.LoaderForFonts;
import com.sasaarsenovic.customwidgetslibrary.R;
import com.sasaarsenovic.customwidgetslibrary.utils.ColorUtil;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.DKGRAY;
import static android.view.View.LAYER_TYPE_SOFTWARE;

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

    private int currentDateTextColor = Color.WHITE;
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
    private int otherMonthDaysColor = Color.BLACK;
    private boolean shouldSelectFirstDayOfMonthOnScroll = true;
    private boolean shouldPaintWeekendDaysForOtherMonths = false;
    private boolean shouldDisplayDividerForRows = false;
    private int colorForWeekendDays;
    private int rowsDividerColor;
    private float rowsDividerHeight = 1;
    private boolean show3DEffect = false;
    private boolean showParallaxEffect = false;
    private float densityParallax1, densityParallax15, densityParallax2;
    private Paint.Style currentDayIndicatorStyle;
    private IndicatorShapes currentDayIndicatorShape;
    private boolean shouldShowIndicatorForCurrentDay = false;
    private int currentDayIndicatorColor;
    private boolean showGridView = false;
    private int gridViewColor;
    private int customDayColumnColor;
    private boolean customDayColumnColorForOtherMonthDays = false;
    private boolean shouldPaintCustomDayColumnColorForDayName = false;
    private boolean shouldDrawCustomDayColumnColor = false;

    private Date[] customDatesForCustomDrawing;
    private int customDaysTextColor;
    private Paint.Style customDaysPaintStyle;

    private OverScroller overScroller;
    private Rect textSizeRect;
    private Locale locale;
    private TimeZone timeZone;
    private Typeface typeface;
    private Calendar currentCalendar;
    private Calendar todayCalendar;
    private Calendar previousMonthCalendar;
    private Paint dayPaint = new Paint();
    private Paint shadowPaint;
    private Context context;
    private float sensor_x, sensor_y, sensor_z;
    private Paint parallaxPaint;
    private Path path;

    ITSCustomCalendarController(Context context, Locale locale, TimeZone timeZone, AttributeSet attrs, OverScroller overScroller, Paint dayPaint,
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

    //Loading attributes from xml
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

                colorForWeekendDays = typedArray.getColor(R.styleable.ITSCustomCalendarView_calendarWeekendDaysColor, colorForWeekendDays);

                textSize = typedArray.getDimensionPixelSize(R.styleable.ITSCustomCalendarView_calendarTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.ITSCustomCalendarView_targetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
            } finally {
                typedArray.recycle();
            }
        }
    }

    //Initialization on ITSCustomCalendarController
    private void init(Context context) {
        if (context != null) {
            colorForWeekendDays = calendarDatesTextColor;
            shadowPaint = new Paint();
            parallaxPaint = new Paint();

            densityParallax1 = Calculations.getPxFromDp(context, 1);
            densityParallax15 = Calculations.getPxFromDp(context, 1.5f);
            densityParallax2 = Calculations.getPxFromDp(context, 2);

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
            textHeight = textSizeRect.height();
            textWidth = textSizeRect.width();
//            textHeight = textSizeRect.height() * 3;
//            textWidth = textSizeRect.width() * 2;

            todayCalendar.setTime(new Date());
            currentCalendar.setTime(currentDate);

            setCalendarToMidnight(todayCalendar);
            setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
            setUsingThreeLetterForWeek(false);

            initScreenDensityRelatedValues(context);

            xIndicatorOffset = 3.5f * screenDensity;
            smallIndicatorRadius = 2.5f * screenDensity;
            growFactor = Integer.MAX_VALUE;

            gridViewColor = calendarDatesTextColor;
            customDayColumnColor = calendarDatesTextColor;
            path = new Path();
        } else {
            throw new NullPointerException();
        }
    }

    void onDraw(Canvas canvas) {
        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        calculateXPositionOfset();

        drawCalendarBackground(canvas);
        drawScrollableCalendar(canvas);
    }

    void onSingleTapUp(MotionEvent e) {

    }

    void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
        widthPerDay = (width) / DAYS_IN_WEEK; //91
        heightPerDay = targetHeight > 0 ? targetHeight / 7 : height / 7; //1282 / 7 (targetHeight doesn't defined)
        this.width = width; //640
        this.distanceThresholdForAutoScroll = (int) (width * 0.50); //320
        this.height = height; //1124
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;

        //makes easier to find radius
//        bigCircleIndicatorRadius = getInterpolatedBigCircleIndicator();

        // scale the selected day indicators slightly so that event indicators can be drawn below
//        bigCircleIndicatorRadius = shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == CompactCalendarView.SMALL_INDICATOR ? bigCircleIndicatorRadius * 0.85f : bigCircleIndicatorRadius;
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
        } else {
            throw new NullPointerException();
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

    //Draws next month
    private void drawNextMonth(Canvas canvas, int offset) {
        setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, offset);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    //Reset calendar to first month day
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

    //Returns day of week
    private int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeekToDraw;
        dayOfWeek = dayOfWeek < 0 ? 7 + dayOfWeek : dayOfWeek;
        return dayOfWeek;
    }

    //Draws calendar month on canvas with custom parameters
    private void drawMonth(Canvas canvas, Calendar monthForDrawCal, int offset) {
        int firstDayOfMonth = getDayOfWeek(monthForDrawCal);
        boolean isWeekend = false;

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
                dayColumn++;
            }

            isWeekend = getIsWeekend(firstDayOfWeekToDraw, dayColumn);

            if (dayColumn == dayColumnNames.length) {
                break;
            }

            //Reset text height
            resetTextHeight(dayPaint, "31");

            float xPosition = widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
            float yPosition = heightPerDay * dayRow + paddingHeight + textHeight / 2;

            if (dayRow > 0 && dayRow < 7) {
                if (shouldDisplayDividerForRows) {
                    dayPaint.setColor(rowsDividerColor);
                    canvas.drawRect(new RectF(0, yPosition + 20, width, yPosition + 20 + rowsDividerHeight), dayPaint);
                    dayPaint.setColor(calendarWeekDaysTextColor);
                }
            }

            //Show grid view arround days
            if (showGridView) {
                if (dayRow > 0) {
                    if (dayColumn == 0 || dayColumn == 1 || dayColumn == 2 || dayColumn == 3 || dayColumn == 4 || dayColumn == 5 || dayColumn == 6) {
                        dayPaint.setColor(gridViewColor);
                        if (typeface != null) {
                            dayPaint.setTypeface(typeface);
                        } else {
                            dayPaint.setTypeface(Typeface.DEFAULT);
                        }
                        dayPaint.setStyle(Paint.Style.STROKE);
                        drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow, widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1), dayPaint);
                    }
                    dayPaint.setColor(calendarWeekDaysTextColor); //Reset color
                }
            }

            if (dayRow == 0) {
                if (shouldDrawDaysHeader) {
                    dayPaint.setColor(calendarWeekDaysTextColor);
                    if (typeface != null) {
                        dayPaint.setTypeface(typeface);
                    } else {
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                    dayPaint.setStyle(Paint.Style.FILL);

                    if (show3DEffect) {
                        draw3DEffect(canvas, shadowPaint, String.valueOf(dayColumnNames[columnDirection]), xPosition, yPosition);
                    }

                    if (showParallaxEffect) {
                        drawParallaxShadowEffect(canvas, parallaxPaint, String.valueOf(dayColumnNames[columnDirection]), xPosition, yPosition);
                    }

                    //Reset text height
                    resetTextHeight(dayPaint, dayColumnNames[columnDirection]);
                    canvas.drawText(dayColumnNames[columnDirection], xPosition, paddingHeight + textHeight / 2, dayPaint);

                    //Custom drawing day column
                    if (shouldDrawCustomDayColumnColor) {
                        if (shouldPaintCustomDayColumnColorForDayName) {
                            int selectedColumn = getDayColumn(getFirstDayOfWeek());
                            dayPaint.setColor(customDayColumnColor);
                            if (dayColumn == selectedColumn) {
                                canvas.drawText(dayColumnNames[columnDirection], xPosition, paddingHeight + textHeight / 2, dayPaint);
                            }
                        }
                        dayPaint.setColor(calendarWeekDaysTextColor); //Reset color
                    }

                    dayPaint.setTypeface(Typeface.DEFAULT); //Reset typeface

                    if (shouldDrawLineDividerUnderWeekDaysHeader) {
                        dayPaint.setColor(lineDividerUnderWeekDaysHeaderColor);
                        canvas.drawRect(new RectF(0, paddingHeight + 20 + textHeight / 2, width, paddingHeight + 20 + lineDividerUnderWeekDaysHeaderHeight + textHeight / 2), dayPaint);
                        dayPaint.setColor(calendarWeekDaysTextColor); //Reset day paint color
                    }
                }
            } else {
                int day = ((dayRow - 1) * 7 + columnDirection + 1) - firstDayOfMonth;
                if (currentCalendar.get(Calendar.DAY_OF_MONTH) == day && isSameMonthAsCurrentCalendar) {
                    //obelezi ovaj datum
                } else if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day) {
                    //todaysDate
                }

                if (day <= 0) {
                    //previous month days
                    if (displayOtherMonthDays) {
                        dayPaint.setStyle(Paint.Style.FILL);
                        if (shouldPaintWeekendDaysForOtherMonths) {
                            if (isWeekend)
                                dayPaint.setColor(colorForWeekendDays);
                            else
                                dayPaint.setColor(otherMonthDaysColor);
                        } else {
                            dayPaint.setColor(otherMonthDaysColor);
                        }
                        if (typeface != null) {
                            dayPaint.setTypeface(typeface);
                        } else {
                            dayPaint.setTypeface(Typeface.DEFAULT);
                        }

                        if (show3DEffect) {
                            draw3DEffect(canvas, shadowPaint, String.valueOf(maxPreviousMonthDay + day), xPosition, yPosition);
                        }

                        if (showParallaxEffect) {
                            drawParallaxShadowEffect(canvas, parallaxPaint, String.valueOf(maxPreviousMonthDay + day), xPosition, yPosition);
                        }

                        canvas.drawText(String.valueOf(maxPreviousMonthDay + day), xPosition, yPosition, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else if (day > maxMonthDay) {
                    //next month days
                    if (displayOtherMonthDays) {
                        dayPaint.setStyle(Paint.Style.FILL);
                        if (shouldPaintWeekendDaysForOtherMonths) {
                            if (isWeekend)
                                dayPaint.setColor(colorForWeekendDays);
                            else
                                dayPaint.setColor(otherMonthDaysColor);
                        } else {
                            dayPaint.setColor(otherMonthDaysColor);
                        }
                        if (typeface != null) {
                            dayPaint.setTypeface(typeface);
                        } else {
                            dayPaint.setTypeface(Typeface.DEFAULT);
                        }

                        if (show3DEffect) {
                            draw3DEffect(canvas, shadowPaint, String.valueOf(day - maxMonthDay), xPosition, yPosition);
                        }

                        if (showParallaxEffect) {
                            drawParallaxShadowEffect(canvas, parallaxPaint, String.valueOf(day - maxMonthDay), xPosition, yPosition);
                        }

                        canvas.drawText(String.valueOf(day - maxMonthDay), xPosition, yPosition, dayPaint);
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                } else {
                    //Current month
                    dayPaint.setStyle(Paint.Style.FILL);

                    if (shadowPaint == null) {
                        shadowPaint = new Paint();
                    }

                    if (shouldShowIndicatorForCurrentDay) {
                        if (todayDayOfMonth == day && isSameMonthAsToday && isSameMonthAsToday) {
                            //draws indicator shape for current day
                            shadowPaint.setColor(currentDayIndicatorColor);
                            shadowPaint.setTextAlign(Paint.Align.CENTER);
                            shadowPaint.setStyle(currentDayIndicatorStyle);
                            shadowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

                            //Ovde mogu da stavljam text height zato sto znam da su datumi uvek brojevi a resetovan je na pocetku for petlje (ne moram da uzimam textHeight za shadowPaint)
                            switch (currentDayIndicatorShape) {
                                case CIRCLE:
                                    if (!(targetHeight > 0))
                                        drawCircleIndicator(canvas, widthPerDay, xPosition, yPosition - textHeight / 2, shadowPaint);
                                    else
                                        drawCircleIndicator(canvas, heightPerDay, xPosition, yPosition - textHeight / 2, shadowPaint);
                                    break;
                                case CIRCLE_WITH_SHADOW:
                                    if (!(targetHeight > 0))
                                        drawCircleIndicatorWithShadow(canvas, widthPerDay, xPosition, yPosition - textHeight / 2, shadowPaint);
                                    else
                                        drawCircleIndicatorWithShadow(canvas, heightPerDay, xPosition, yPosition - textHeight / 2, shadowPaint);
                                    break;
                                case RECTANGLE:
                                    drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow, widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1), shadowPaint);
                                    break;
                                case RECTANGLE_WITH_SHADOW:
                                    drawRectangleIndicatorWithShadow(canvas, widthPerDay * dayColumn, heightPerDay * dayRow, widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1), shadowPaint);
                                    break;
                                case SQUARE:
                                    if (!(targetHeight > 0))
                                        drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow + (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2),
                                                widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1) - (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2), shadowPaint);
                                    else {
                                        if (heightPerDay >= widthPerDay) {
                                            drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow + (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2),
                                                    widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1) - (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2), shadowPaint);
                                        } else {
                                            drawRectangleIndicator(canvas, widthPerDay * dayColumn + (((widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn) - heightPerDay) / 2),
                                                    heightPerDay * dayRow,
                                                    widthPerDay * (dayColumn + 1) - (((widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn) - heightPerDay) / 2), heightPerDay * (dayRow + 1), shadowPaint);
                                        }
                                    }
                                    break;
                                case SQUARE_WITH_SHADOW:
                                    //TODO: finish shadow view
                                    if (!(targetHeight > 0))
                                        drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow + (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2),
                                                widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1) - (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2), shadowPaint);
                                    else {
                                        if (heightPerDay >= widthPerDay) {
                                            drawRectangleIndicator(canvas, widthPerDay * dayColumn, heightPerDay * dayRow + (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2),
                                                    widthPerDay * (dayColumn + 1), heightPerDay * (dayRow + 1) - (((heightPerDay * (dayRow + 1) - heightPerDay * dayRow) - (widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn)) / 2), shadowPaint);
                                        } else {
                                            drawRectangleIndicator(canvas, widthPerDay * dayColumn + (((widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn) - heightPerDay) / 2),
                                                    heightPerDay * dayRow,
                                                    widthPerDay * (dayColumn + 1) - (((widthPerDay * (dayColumn + 1) - widthPerDay * dayColumn) - heightPerDay) / 2), heightPerDay * (dayRow + 1), shadowPaint);
                                        }
                                    }
                                    break;
                                case TRIANGLE:
                                    //drawTriangleIndicator(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case TRIANGLE_WITH_SHADOW:
                                    //drawTriangleIndicatorWithShadow(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case INVERSE_TRIANGLE:
                                    //drawInverseTriangleIndicator(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case INVERSE_TRIANGLE_WITH_SHADOW:
                                    //drawInverseTriangleIndicatorWithShadow(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case STAR:
                                    drawStarIndicator(canvas, dayColumn, dayRow, shadowPaint);
                                    break;
//                                case STAR_WITH_SHADOW:
//                                    //drawStarIndicatorWithShadow(canvas, circleRadius, cx, cy, paint);
//                                    break;
                                case HEXAGON:
                                    //drawStarIndicator(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case HEXAGON_WITH_SHADOW:
                                    //drawStarIndicatorWithShadow(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case OCTAGON:
                                    //drawStarIndicator(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case OCTAGON_WITH_SHADOW:
                                    //drawStarIndicatorWithShadow(canvas, circleRadius, cx, cy, paint);
                                    break;
                                case UNDERLINED:
                                    drawRectangleIndicator(canvas, widthPerDay * dayColumn, yPosition + textHeight / 2 + Calculations.getPxFromDp(context, 2f), widthPerDay * (dayColumn + 1), yPosition + textHeight / 2 + Calculations.getPxFromDp(context, 5f), shadowPaint);
                                    break;
                                default:
                                    drawCircleIndicator(canvas, widthPerDay, xPosition, yPosition, shadowPaint);
                            }
                            shadowPaint.setColor(DKGRAY);
                        }
                    }

                    if (isWeekend) {
                        if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day) {
                            dayPaint.setColor(currentDateTextColor);
                        } else {
                            dayPaint.setColor(colorForWeekendDays);
                        }
                    } else {
                        if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day) {
                            dayPaint.setColor(currentDateTextColor);
                        } else {
                            dayPaint.setColor(calendarDatesTextColor);
                        }
                    }

                    if (typeface != null) {
                        dayPaint.setTypeface(typeface);
                    } else {
                        dayPaint.setTypeface(Typeface.DEFAULT);
                    }
                    if (show3DEffect) {
                        draw3DEffect(canvas, shadowPaint, String.valueOf(day), xPosition, yPosition);
                    }

                    if (showParallaxEffect) {
                        drawParallaxShadowEffect(canvas, parallaxPaint, String.valueOf(day), xPosition, yPosition);
                    }
                    canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                    dayPaint.setTypeface(Typeface.DEFAULT); //Reset typeface
                }
            }
            canvas.save();
        }
        canvas.restore();
    }

    private void resetTextHeight(Paint paint, String text) {
        paint.getTextBounds(text, 0, text.length(), textSizeRect);
        textHeight = textSizeRect.height();
    }

    //Returns boolean if is weekend day row X column
    private boolean getIsWeekend(int firstDayOfWeek, int dayColumn) {
        if (!isRtl) {
            switch (firstDayOfWeek) {
                case Calendar.MONDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.MONDAY) || dayColumn == getColumnForCustomDay(Calendar.MONDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.TUESDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.TUESDAY) || dayColumn == getColumnForCustomDay(Calendar.TUESDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.WEDNESDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.WEDNESDAY) || dayColumn == getColumnForCustomDay(Calendar.WEDNESDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.THURSDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.THURSDAY) || dayColumn == getColumnForCustomDay(Calendar.THURSDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.FRIDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.FRIDAY) || dayColumn == getColumnForCustomDay(Calendar.FRIDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.SATURDAY:
                    if (dayColumn == getColumnForCustomDay(Calendar.SATURDAY) || dayColumn == getColumnForCustomDay(Calendar.SATURDAY) + 1)
                        return true;
                    else
                        return false;
                case Calendar.SUNDAY:
                    if (dayColumn == 0 || dayColumn == getColumnForCustomDay(Calendar.SUNDAY))
                        return true;
                    else
                        return false;
            }
        } else {
            switch (firstDayOfWeek) {
                case Calendar.SUNDAY:
                    if (dayColumn == 0 || dayColumn == getColumnForCustomDay(Calendar.SUNDAY))
                        return true;
                    else
                        return false;
                case Calendar.SATURDAY:
                    if (dayColumn == 5 || dayColumn == 6)
                        return true;
                    else
                        return false;
                case Calendar.FRIDAY:
                    if (dayColumn == 4 || dayColumn == 5)
                        return true;
                    else
                        return false;
                case Calendar.THURSDAY:
                    if (dayColumn == 3 || dayColumn == 4)
                        return true;
                    else
                        return false;
                case Calendar.WEDNESDAY:
                    if (dayColumn == 2 || dayColumn == 3)
                        return true;
                    else
                        return false;
                case Calendar.TUESDAY:
                    if (dayColumn == 1 || dayColumn == 2)
                        return true;
                    else
                        return false;
                case Calendar.MONDAY:
                    if (dayColumn == 0 || dayColumn == 1)
                        return true;
                    else
                        return false;
            }
        }
        return false;
    }

    private int getDayColumn(int selectedDay) {
        if (!isRtl) {
            switch (getFirstDayOfWeek()) {
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                case 5:
                    return 5;
                case 6:
                    return 6;
                case 7:
                    return 7;
            }
        } else {
            return 0;
        }

        return 0;
    }

    //Returns column for selected day
    private int getColumnForCustomDay(int day) {
        return DAYS_IN_WEEK - day;
    }

    //Draws 3d effect shadow on calendar
    private void draw3DEffect(Canvas canvas, Paint paint, String day, float xPosition, float yPosition) {
        if (paint == null)
            paint = new Paint();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        if (typeface != null) {
            paint.setTypeface(typeface);
        } else {
            paint.setTypeface(Typeface.SANS_SERIF);
        }
        paint.setTextSize(textSize);
        paint.setColor(DKGRAY);
        paint.getTextBounds("31", 0, "31".length(), textSizeRect);
        canvas.drawText(day, xPosition + 2, yPosition - 2, paint);
        canvas.drawText(day, xPosition + 3, yPosition - 3, paint);
        canvas.drawText(day, xPosition + 4, yPosition - 4, paint);
        paint.setTypeface(Typeface.DEFAULT);
    }

    //Draws parallax effect shadow on calendar
    private void drawParallaxShadowEffect(Canvas canvas, Paint paint, String day, float xPosition, float yPosition) {
        if (paint == null)
            paint = new Paint();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        if (typeface != null) {
            paint.setTypeface(typeface);
        } else {
            paint.setTypeface(Typeface.SANS_SERIF);
        }
        paint.setTextSize(textSize);
        paint.setColor(DKGRAY);

        paint.getTextBounds("31", 0, "31".length(), textSizeRect);
        canvas.drawText(String.valueOf(day), xPosition + (-sensor_x), yPosition + sensor_y, paint);
        canvas.drawText(String.valueOf(day), xPosition + densityParallax1 + (-sensor_x), yPosition + densityParallax1 + sensor_y, paint);
        canvas.drawText(String.valueOf(day), xPosition + densityParallax15 + (-sensor_x), yPosition + densityParallax15 + sensor_y, paint);
        canvas.drawText(String.valueOf(day), xPosition + densityParallax2 + (-sensor_x), yPosition + densityParallax2 + sensor_y, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(calendarDatesTextColor);
    }

    //Draws circle shape indicator on canvas
    private void drawCircleIndicator(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
        canvas.drawCircle(cx, cy, circleRadius / 2, paint);
    }

    //Draws circle shape indicator with shadow on canvas
    private void drawCircleIndicatorWithShadow(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
//        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.3));
//        canvas.drawCircle(cx, cy, circleRadius / 2, paint);
//        paint.setColor(currentDayIndicatorColor);
//        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.15));
//        canvas.drawCircle(cx, cy, circleRadius / 2 - 5, paint);
//        paint.setColor(currentDayIndicatorColor);
//        canvas.drawCircle(cx, cy, circleRadius / 2 - 10, paint);

        for (float i = 10, j = 0.4f; i > 0; i--, j -= 0.05) {
            paint.setColor(currentDayIndicatorColor);
            paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), j));
            if (i == 1)
                paint.setColor(currentDayIndicatorColor);
            canvas.drawCircle(cx, cy, (circleRadius / 2 - 8) + i, paint);
        }
        paint.setColor(currentDayIndicatorColor);
    }

    //Draws rectangle shape indicator on canvas
    private void drawRectangleIndicator(Canvas canvas, float start_x, float start_y, float end_x, float end_y, Paint paint) {
        canvas.drawRect(new RectF(start_x, start_y, end_x, end_y), paint);
    }

    //Draws rectangle shape indicator with shadow on canvas
    private void drawRectangleIndicatorWithShadow(Canvas canvas, float start_x, float start_y, float end_x, float end_y, Paint paint) {
//        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.3));
//        canvas.drawRect(new RectF(start_x, start_y, end_x, end_y), paint);
//        paint.setColor(currentDayIndicatorColor);
//        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.15));
//        canvas.drawRect(new RectF(start_x + 10, start_y + 10, end_x - 10, end_y - 10), paint);
//        paint.setColor(currentDayIndicatorColor);
//        canvas.drawRect(new RectF(start_x + 20, start_y + 20, end_x - 20, end_y - 20), paint);

        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.3));
        canvas.drawRect(new RectF(start_x, start_y, end_x, end_y), paint);
        paint.setColor(currentDayIndicatorColor);
        paint.setColor(ColorUtil.getDarkerColor(paint.getColor(), 0.15));
        canvas.drawRect(new RectF(start_x + (!isContextNull() ? Calculations.getPxFromDp(context, 1) : 3), start_y + (!isContextNull() ? Calculations.getPxFromDp(context, 1) : 3),
                end_x - (!isContextNull() ? Calculations.getPxFromDp(context, 1) : 3), end_y - (!isContextNull() ? Calculations.getPxFromDp(context, 1) : 3)), paint);
        paint.setColor(currentDayIndicatorColor);
        canvas.drawRect(new RectF(start_x + (!isContextNull() ? Calculations.getPxFromDp(context, 2) : 6), start_y + (!isContextNull() ? Calculations.getPxFromDp(context, 2) : 6),
                end_x - (!isContextNull() ? Calculations.getPxFromDp(context, 2) : 6), end_y - (!isContextNull() ? Calculations.getPxFromDp(context, 2) : 6)), paint);
    }

    //Draws square shape indicator on canvas
    private void drawSquareIndicator(Canvas canvas, float start_x, float start_y, float end_x, float end_y, Paint paint) {
        canvas.drawRect(new RectF(start_x, start_y, end_x, end_y), paint);
    }

    //Draws square shape indicator with shadow on canvas
    private void drawSquareIndicatorWithShadow(Canvas canvas, float start_x, float start_y, float end_x, float end_y, Paint paint) {
        canvas.drawRect(new RectF(start_x, start_y, end_x, end_y), paint);
    }

    //Draws triangle shape indicator on canvas
    private void drawTriangleIndicator(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
        //TODO: change method attributes
    }

    //Draws triangle shape indicator with shadow on canvas
    private void drawTriangleIndicatorWithShadow(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
        //TODO: change method attributes
    }

    //Draws inverse triangle shape indicator on canvas
    private void drawInverseTriangleIndicator(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
        //TODO: change method attributes
    }

    //Draws inverse triangle shape indicator with shadow on canvas
    private void drawInverseTriangleIndicatorWithShadow(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
        //TODO: change method attributes
    }

    //Draws star shape indicator on canvas
    private void drawStarIndicator(Canvas canvas, int dayColumn, int dayRow, Paint paint) {
        if (path == null)
            path = new Path();

        if (heightPerDay >= widthPerDay) {
            float half = (widthPerDay * (dayColumn + 1)) / 2;
            float min = Math.min(widthPerDay, heightPerDay);

            float x = (widthPerDay * (dayColumn + 1)) / 2;
            float y = (heightPerDay * (dayRow));
            float y2 = (heightPerDay * (dayRow + 1));

            // top left
            path.moveTo(x, y + y * 0.077f);

            // top right
            path.lineTo(x * 2, y + y * 0.077f);

            // bottom left
//        path.lineTo(x + x * 0.02f, y2 - y * 0.02f);
            path.lineTo(x + x / 2 - x * 0.35f, y2 - y * 0.043f);

            // top tip
            path.lineTo(x + x / 2, y + y * 0.03f);

            // bottom right
//        path.lineTo(x * 2 - x * 0.02f, y2 - y * 0.02f);
            path.lineTo(x + x / 2 + x * 0.35f, y2 - y * 0.043f);

            // top left
            path.lineTo(x, y + y * 0.077f);

            path.close();
            canvas.drawPath(path, shadowPaint);
        } else {
            float x = (widthPerDay * (dayColumn + 1)) / 2;
            float y = (heightPerDay * (dayRow));
            float y2 = (heightPerDay * (dayRow + 1));

            // top left
            path.moveTo(x + x * 0.26f, y + y * 0.067f);

            // top right
            path.lineTo(x * 2 - x * 0.26f, y + y * 0.067f);

            // bottom left
            path.lineTo(x + x / 2 - x * 0.15f, y2);

            // top tip
            path.lineTo(x + x / 2, y);

            // bottom right
            path.lineTo(x + x / 2 + x * 0.15f, y2);

            // top left
            path.moveTo(x + x * 0.26f, y + y * 0.067f);

            path.close();
            canvas.drawPath(path, shadowPaint);
        }
    }

    //Draws star shape indicator with shadow on canvas
//    private void drawStarIndicatorWithShadow(Canvas canvas, float circleRadius, float cx, float cy, Paint paint) {
//        //TODO: change method attributes
//    }

    //Draws underline shape indicator on canvas
    private void drawUnderlinedIndicator(Canvas canvas, float start_x, float start_y, float end_x, float end_y, Paint paint) {
        canvas.drawLine(start_x, start_y, end_x, end_y, paint);
    }

    private int computeVelocity() {
        velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity);
        return (int) velocityTracker.getXVelocity();
    }

    private boolean isContextNull() {
        return context == null;
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

    int getFirstDayOfWeek() {
        return firstDayOfWeekToDraw;
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
        int targetScroll = (monthsScrolledSoFar) * width;
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

    private Date getFirstDayOfCurrentMonth() {
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
//        this.targetHeight = targetHeight;

        try {
            this.targetHeight = (int) Calculations.getPxFromDp(context, targetHeight);
        } catch (Exception e) {
            throw new ClassCastException(e.getMessage());
        }
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

    void setCalendarDatesTextColor(int calendarDatesTextColor) {
        this.calendarDatesTextColor = calendarDatesTextColor;
    }

    void setCalendarWeekDaysTextColor(int calendarWeekDaysTextColor) {
        this.calendarWeekDaysTextColor = calendarWeekDaysTextColor;
    }


    void setSelectedDateTextColor(int selectedDateTextColor) {
        this.selectedDateTextColor = selectedDateTextColor;
    }

    void setCurrentDateTextColor(int currentDateTextColor) {
        this.currentDateTextColor = currentDateTextColor;
    }

    void setCurrentDate(Date date) {
        distanceX = 0;
        monthsScrolledSoFar = 0;
        accumulatedScrollOffset.x = 0;
        overScroller.startScroll(0, 0, 0, 0);
        currentDate = new Date(date.getTime());
        currentCalendar.setTime(currentDate);
        todayCalendar = Calendar.getInstance();
        setCalendarToMidnight(currentCalendar);
    }

    String getCurrentMonthName() {
        return getMonthName(currentCalendar);
    }

    String getMonthNameForSelectedDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMonthName(calendar);
    }

    private String getMonthName(Calendar calendar) {
        String month = "Unknown";
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] months;
        if (useThreeLetterAbbreviation)
            months = dateFormatSymbols.getShortMonths();
        else
            months = dateFormatSymbols.getMonths();

        int monthNum = calendar.get(Calendar.MONTH);
        if (monthNum >= 0 && monthNum <= 11) {
            month = months[monthNum];
        }
        return month;
    }


    void setOtherMonthDaysColor(int otherMonthDaysColor) {
        this.otherMonthDaysColor = otherMonthDaysColor;
    }

    String getYearStringForCurrentMonth() {
        return String.valueOf(currentCalendar.get(Calendar.YEAR));
    }

    String getYearStringForSelectedDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    private void scrollNext() {
        monthsScrolledSoFar = monthsScrolledSoFar - 1;
        accumulatedScrollOffset.x = monthsScrolledSoFar * width;
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalendar.getTime(), 0, 1);
            setCurrentDate(calendarWithFirstDayOfMonth.getTime());
        }
        performMonthScrollCallback();
    }

    private void scrollPrevious() {
        monthsScrolledSoFar = monthsScrolledSoFar + 1;
        accumulatedScrollOffset.x = monthsScrolledSoFar * width;
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalendarToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalendar.getTime(), 0, -1);
            setCurrentDate(calendarWithFirstDayOfMonth.getTime());
        }
        performMonthScrollCallback();
    }

    void scrollToRight() {
        if (isRtl)
            scrollPrevious();
        else
            scrollNext();
    }

    void scrollToLeft() {
        if (isRtl)
            scrollNext();
        else
            scrollPrevious();
    }

    void setShouldSelectFirstDayOfMonthOnScroll(boolean shouldSelectFirstDayOfMonthOnScroll) {
        this.shouldSelectFirstDayOfMonthOnScroll = shouldSelectFirstDayOfMonthOnScroll;
    }

    void setColorForWeekendDays(int colorForWeekendDays) {
        this.colorForWeekendDays = colorForWeekendDays;
    }

    void shouldPaintWeekendDaysForOtherMonths(boolean shouldPaintWeekendDaysForOtherMonths) {
        this.shouldPaintWeekendDaysForOtherMonths = shouldPaintWeekendDaysForOtherMonths;
    }

    void shouldDisplayDividerForRows(boolean shouldDisplayDividerForRows) {
        this.shouldDisplayDividerForRows = shouldDisplayDividerForRows;
    }

    void setRowsDividerColor(int rowsDividerColor) {
        this.rowsDividerColor = rowsDividerColor;
    }

    void setRowsDividerHeight(int rowsDividerHeightInDp) {
        this.rowsDividerHeight = Calculations.getPxFromDp(context, rowsDividerHeightInDp);
    }

    void show3DEffect(boolean show3DEffect) {
        this.show3DEffect = show3DEffect;
    }

    void onSensorEvent(SensorEvent sensorEvent) {
        sensor_x = sensorEvent.values[0];
        sensor_y = sensorEvent.values[1];
//        sensor_z = sensor_z - sensorEvent.values[2];

        System.out.println("test");
    }

    void showParallaxEffect(boolean showParallaxEffect) {
        this.showParallaxEffect = showParallaxEffect;
    }

    void setSelectedDateIndicatorStyle(int selectedDateIndicatorStyle) {

    }

    void setCurrentDayIndicatorStyle(Paint.Style currentDayIndicatorStyle) {
        this.currentDayIndicatorStyle = currentDayIndicatorStyle;
    }

    void setSelectedDateIndicatorShape(IndicatorShapes indicatorShape) {

    }

    void setCurrentDayIndicatorShape(IndicatorShapes indicatorShape) {
        this.currentDayIndicatorShape = indicatorShape;
    }

    void shouldShowIndicatorForCurrentDay(boolean shouldShowIndicatorForCurrentDay) {
        this.shouldShowIndicatorForCurrentDay = shouldShowIndicatorForCurrentDay;
    }

    void setCurrentDayIndicatorColor(int currentDayIndicatorColor) {
        this.currentDayIndicatorColor = currentDayIndicatorColor;
    }


    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Custom days params>
    //TODO: URADITI OVU SEKCIJU

    void setCustomDaysForCustomDrawing(Date[] dates) {
        this.customDatesForCustomDrawing = dates;
    }

    void setCustomDaysPaintStyle(Paint.Style paintStyle) {
        this.customDaysPaintStyle = paintStyle;
    }

    void setCustomDaysTextColor(int customDaysTextColor) {
        this.customDaysTextColor = customDaysTextColor;
    }

    //</Custom days params>
    //--------------------------------------------------------------------------------

    //********************************************************************************
    //********************************************************************************
    //********************************************************************************
    //<Custom days indicator shape params>
    //TODO: URADITI OVU SEKCIJU

    void setCustomDaysForCustomDrawingShapes(Date[] dates) {

    }

    void setCustomDaysIndicatorShape(IndicatorShapes indicatorShape) {

    }

    void setCustomDaysIndicatorShapePaintStyle(Paint.Style customDayIndicatorShapePaintStyle) {

    }

    void setCustomDayIndicatorShapeColor(int customDayIndicatorShapeColor) {

    }

    void shouldShowIndicatorShapeForCustomDay(boolean shouldShowIndicatorShapeForCustomDay) {

    }

    //</Custom days indicator shape params>
    //--------------------------------------------------------------------------------

    void setShowGridView(boolean showGridView) {
        this.showGridView = showGridView;
    }

    void setGridViewColor(int gridViewColor) {
        this.gridViewColor = gridViewColor;
    }

    //Color for custom day column
    void setCustomDayColumnColor(int customDayColumnColor) {
        this.customDayColumnColor = customDayColumnColor;
    }


    void shouldDrawCustomDayColumnColorForOtherMonthDays(boolean customDayColumnColorForOtherMonthDays) {
        this.customDayColumnColorForOtherMonthDays = customDayColumnColorForOtherMonthDays;
    }

    void shouldPaintCustomDayColumnColorForDayName(boolean shouldPaintCustomDayColumnColorForDayName) {
        this.shouldPaintCustomDayColumnColorForDayName = shouldPaintCustomDayColumnColorForDayName;
    }

    void shouldDrawCustomDayColumnColor(boolean shouldDrawCustomDayColumnColor) {
        this.shouldDrawCustomDayColumnColor = shouldDrawCustomDayColumnColor;
    }
}
