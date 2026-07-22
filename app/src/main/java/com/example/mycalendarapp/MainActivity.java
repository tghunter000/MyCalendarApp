package com.example.mycalendarapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {

    TextView txtDayTitle, txtGoal, txtQuote;
    LinearLayout exerciseContainer, dayButtonContainer;
    String[] quotes;
    Random random = new Random();
    int selectedDay = 0;

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

        quotes = getResources().getStringArray(R.array.motivational_quotes);

        setupWeekPlan();
        setupDayButtons();
        showDay(0);

        btnNewQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRandomQuote();
            }
        });
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
        txtGoal.setText("🎯 " + day.goal);

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
