package com.example.mycalendarapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    TextView txtDayTitle, txtGoal, txtQuote, txtMonthYear;
    LinearLayout exerciseContainer, dayButtonContainer, calendarGrid;
    String[] quotes;
    Random random = new Random();
    int selectedDay = 0;

    int viewYear, viewMonth;
    int todayYear, todayMonth, todayDate;
    int selectedYear, selectedMonth, selectedDate;

    Map<String, String> festivals = new HashMap<>();

    String[] weekdayShort = {"S", "M", "T", "W", "T", "F", "S"};
    String[] monthNames = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    class WorkoutDay {
        String name, goal;
        String[] exercises;
        WorkoutDay(String name, String goal, String[] exercises) {
            this.name = name;
            this.goal = goal;
            this.exercises = exercises;
        }
    }

    WorkoutDay[] weekPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDayTitle = findViewById(R.id.txtDayTitle);
        txtGoal = findViewById(R.id.txtGoal);
        txtQuote = findViewById(R.id.txtQuote);
        exerciseContainer = findViewById(R.id.exerciseContainer);
        dayButtonContainer = findViewById(R.id.dayButtonContainer);
        Button btnNewQuote = findViewById(R.id.btnNewQuote);

        txtMonthYear = findViewById(R.id.txtMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        Button btnPrevMonth = findViewById(R.id.btnPrevMonth);
        Button btnNextMonth = findViewById(R.id.btnNextMonth);

        quotes = getResources().getStringArray(R.array.motivational_quotes);

        setupFestivals();

        Calendar nowCal = Calendar.getInstance();
        todayYear = nowCal.get(Calendar.YEAR);
        todayMonth = nowCal.get(Calendar.MONTH);
        todayDate = nowCal.get(Calendar.DAY_OF_MONTH);
        viewYear = todayYear;
        viewMonth = todayMonth;
        selectedYear = todayYear;
        selectedMonth = todayMonth;
        selectedDate = todayDate;

        setupWeekPlan();
        setupDayButtons();
        buildCalendar();
        showWorkoutForDate(selectedYear, selectedMonth, selectedDate);

        btnNewQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRandomQuote();
            }
        });

        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMonth--;
                if (viewMonth < 0) {
                    viewMonth = 11;
                    viewYear--;
                }
                buildCalendar();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMonth++;
                if (viewMonth > 11) {
                    viewMonth = 0;
                    viewYear++;
                }
                buildCalendar();
            }
        });
    }

    void setupFestivals() {
        festivals.put("2026-1-1", "New Year");
        festivals.put("2026-1-14", "Makar Sankranti / Pongal");
        festivals.put("2026-1-26", "Republic Day");
        festivals.put("2026-3-4", "Holi");
        festivals.put("2026-3-21", "Eid-ul-Fitr*");
        festivals.put("2026-4-14", "Baisakhi / Ambedkar Jayanti");
        festivals.put("2026-4-18", "Good Friday");
        festivals.put("2026-5-1", "Labour Day");
        festivals.put("2026-5-27", "Eid-ul-Adha*");
        festivals.put("2026-8-15", "Independence Day");
        festivals.put("2026-10-2", "Gandhi Jayanti");
        festivals.put("2026-10-20", "Dussehra");
        festivals.put("2026-11-8", "Diwali");
        festivals.put("2026-11-24", "Guru Nanak Jayanti");
        festivals.put("2026-12-25", "Christmas");
    }

    int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    void buildCalendar() {
        calendarGrid.removeAllViews();
        txtMonthYear.setText(monthNames[viewMonth] + " " + viewYear);

        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        for (String d : weekdayShort) {
            TextView tv = new TextView(this);
            tv.setText(d);
            tv.setTextColor(0xFF9AA0A6);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(lp);
            headerRow.addView(tv);
        }
        calendarGrid.addView(headerRow);

        Calendar cal = Calendar.getInstance();
        cal.set(viewYear, viewMonth, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int totalCells = firstDayOfWeek + daysInMonth;
        int totalRows = (int) Math.ceil(totalCells / 7.0);
        int dateCounter = 1;

        for (int r = 0; r < totalRows; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int c = 0; c < 7; c++) {
                int cellIndex = r * 7 + c;
                TextView cell = new TextView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        0, dpToPx(38), 1f);
                lp.setMargins(3, 3, 3, 3);
                cell.setLayoutParams(lp);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(13);

                if (cellIndex < firstDayOfWeek || dateCounter > daysInMonth) {
                    cell.setText("");
                    cell.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    final int thisDate = dateCounter;

                    boolean isToday = (viewYear == todayYear && viewMonth == todayMonth && thisDate == todayDate);
                    boolean isSelected = (viewYear == selectedYear && viewMonth == selectedMonth && thisDate == selectedDate);
                    String fKey = viewYear + "-" + (viewMonth + 1) + "-" + thisDate;
                    boolean hasFestival = festivals.containsKey(fKey);

                    cell.setText(String.valueOf(thisDate));

                    if (isToday) {
                        cell.setBackgroundResource(R.drawable.calendar_today_bg);
                        cell.setTextColor(Color.WHITE);
                    } else if (isSelected) {
                        cell.setBackgroundResource(R.drawable.calendar_selected_bg);
                        cell.setTextColor(Color.WHITE);
                    } else if (hasFestival) {
                        cell.setBackgroundResource(R.drawable.calendar_festival_bg);
                        cell.setTextColor(0xFFFFD54F);
                    } else {
                        cell.setBackgroundColor(Color.TRANSPARENT);
                        cell.setTextColor(Color.WHITE);
                    }

                    final int fY = viewYear;
                    final int fM = viewMonth;
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedYear = fY;
                            selectedMonth = fM;
                            selectedDate = thisDate;
                            buildCalendar();
                            showWorkoutForDate(selectedYear, selectedMonth, selectedDate);
                            showDatePopup(selectedYear, selectedMonth, selectedDate);
                        }
                    });

                    dateCounter++;
                }
                row.addView(cell);
            }
            calendarGrid.addView(row);
        }
    }

    void showWorkoutForDate(int y, int m, int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(y, m, d);
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        int index = (dow + 5) % 7;
        selectedDay = index;
        highlightSelectedButton();
        showDay(index);
    }

    void showDatePopup(final int y, final int m, final int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(y, m, d);
        String dayName = new SimpleDateFormat("EEEE").format(cal.getTime());
        int vikramYear = (m >= 2) ? y + 57 : y + 56;
        String fKey = y + "-" + (m + 1) + "-" + d;
        String festival = festivals.get(fKey);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 30);
        layout.setBackgroundColor(0xFF1C1F26);

        TextView bigDate = new TextView(this);
        bigDate.setText(d + " " + monthNames[m] + " " + y);
        bigDate.setTextColor(0xFFFF6B35);
        bigDate.setTextSize(24);
        bigDate.setTypeface(null, Typeface.BOLD);
        layout.addView(bigDate);

        TextView dayTv = new TextView(this);
        dayTv.setText(dayName);
        dayTv.setTextColor(Color.WHITE);
        dayTv.setTextSize(16);
        dayTv.setPadding(0, 4, 0, 14);
        layout.addView(dayTv);

        TextView vikramTv = new TextView(this);
        vikramTv.setText("Vikram Samvat " + vikramYear + " (approx)");
        vikramTv.setTextColor(0xFFE0E0E0);
        vikramTv.setTextSize(14);
        vikramTv.setPadding(0, 2, 0, 4);
        layout.addView(vikramTv);

        if (festival != null) {
            TextView festTv = new TextView(this);
            festTv.setText("Aaj: " + festival);
            festTv.setTextColor(0xFFFFD54F);
            festTv.setTextSize(15);
            festTv.setPadding(0, 10, 0, 4);
            layout.addView(festTv);
        }

        final String panchangUrl = "https://www.drikpanchang.com/panchang/day-panchang.html?date="
                + String.format("%02d/%02d/%04d", d, m + 1, y);

        Button btnPanchang = new Button(this);
        btnPanchang.setText("View Full Panchang (Tithi, Nakshatra...)");
        btnPanchang.setTextColor(Color.WHITE);
        btnPanchang.setBackgroundResource(R.drawable.day_btn_selected_bg);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(0, 18, 0, 0);
        btnPanchang.setLayoutParams(btnLp);
        btnPanchang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(panchangUrl)));
            }
        });
        layout.addView(btnPanchang);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("Close", null)
                .create();
        dialog.show();
    }

    void setupWeekPlan() {
        weekPlan = new WorkoutDay[7];

        weekPlan[0] = new WorkoutDay("Monday", "Upper Body: Chest & Arms", new String[]{
                "Standard Push-ups — 3 sets x 10-12 reps",
                "Incline Push-ups — 3 sets x 12 reps",
                "Decline Push-ups — 3 sets x 8-10 reps",
                "Resistance Band Chest Flyes — 3 sets x 15 reps",
                "Bicep Curls (Band) — 3 sets x 15 reps"
        });

        weekPlan[1] = new WorkoutDay("Tuesday", "Lower Body: Glutes & Hips", new String[]{
                "Resistance Band Lateral Squats — 3 sets x 15 reps/side",
                "Glute Bridges (Band above knees) — 4 sets x 20 reps",
                "Fire Hydrants — 3 sets x 15 reps/leg",
                "Bulgarian Split Squats — 3 sets x 10 reps/leg"
        });

        weekPlan[2] = new WorkoutDay("Wednesday", "Core & Waist Slimming", new String[]{
                "Standard Crunches — 3 sets x 20 reps",
                "Leg Raises — 3 sets x 15 reps",
                "Bicycle Crunches — 3 sets x 20 reps",
                "Plank Hold — 3 sets x 45s-1min",
                "Vacuum Stomach — 3 sets x 30s holds"
        });

        weekPlan[3] = new WorkoutDay("Thursday", "Shoulders & Back", new String[]{
                "Pike Push-ups — 3 sets x 10 reps",
                "Resistance Band Lateral Raises — 3 sets x 15 reps",
                "Resistance Band Seated Rows — 3 sets x 15 reps",
                "Tricep Dips — 3 sets x 12 reps"
        });

        weekPlan[4] = new WorkoutDay("Friday", "Glutes, Hips & Thighs", new String[]{
                "Resistance Band Romanian Deadlifts — 4 sets x 12 reps",
                "Curtsy Lunges — 3 sets x 12 reps/leg",
                "Donkey Kicks (Band) — 3 sets x 15 reps/leg",
                "Sumo Squats — 4 sets x 15 reps"
        });

        weekPlan[5] = new WorkoutDay("Saturday", "Core & Fat Loss (HIIT)", new String[]{
                "Jumping Jacks — 3 sets x 50 reps",
                "Mountain Climbers — 3 sets x 45s",
                "High Knees — 3 sets x 45s",
                "Bicycle Crunches — 3 sets x 20 reps"
        });

        weekPlan[6] = new WorkoutDay("Sunday", "Active Recovery / Rest Day", new String[]{
                "Light Stretching — 10-15 minutes",
                "Hydrate well & eat high-protein meals",
                "8+ hours of quality sleep",
                "Let your muscles rebuild stronger"
        });
    }

    void setupDayButtons() {
        String[] shortNames = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int i = 0; i < shortNames.length; i++) {
            final int dayIndex = i;
            Button btn = new Button(this);
            btn.setText(shortNames[i]);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(12);
            btn.setBackgroundResource(i == 0 ? R.drawable.day_btn_selected_bg : R.drawable.day_btn_bg);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(6, 0, 6, 0);
            btn.setLayoutParams(params);
            btn.setPadding(20, 10, 20, 10);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedDay = dayIndex;
                    highlightSelectedButton();
                    showDay(dayIndex);
                }
            });

            dayButtonContainer.addView(btn);
        }
    }

    void highlightSelectedButton() {
        for (int i = 0; i < dayButtonContainer.getChildCount(); i++) {
            Button b = (Button) dayButtonContainer.getChildAt(i);
            b.setBackgroundResource(i == selectedDay ? R.drawable.day_btn_selected_bg : R.drawable.day_btn_bg);
        }
    }

    void showDay(int index) {
        WorkoutDay day = weekPlan[index];
        txtDayTitle.setText(day.name);
        txtGoal.setText("Goal: " + day.goal);

        exerciseContainer.removeAllViews();
        for (String exercise : day.exercises) {
            TextView tv = new TextView(this);
            tv.setText("• " + exercise);
            tv.setTextColor(0xFFE0E0E0);
            tv.setTextSize(14);
            tv.setPadding(0, 8, 0, 8);
            exerciseContainer.addView(tv);
        }

        txtQuote.setText("\"" + quotes[index % quotes.length] + "\"");
    }

    void showRandomQuote() {
        String q = quotes[random.nextInt(quotes.length)];
        txtQuote.setText("\"" + q + "\"");
    }
    }
