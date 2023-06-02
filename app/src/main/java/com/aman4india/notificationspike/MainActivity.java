package com.aman4india.notificationspike;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String TAG = "MainActivity";
    private NotificationReceiver notificationReceiver;

    public static void createNotification(Context context) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, SecondActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line... This is example of longer text"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwitchCompat textView = findViewById(R.id.timerSwitch);
        notificationReceiver = new NotificationReceiver();
        textView.setOnClickListener(view -> {
            NotificationReceiver.setReminder(MainActivity.this, NotificationReceiver.class, 12, 46);
//            createNotification();
//            checkNextWorkingDay();
        });
        SwitchCompat cancelSwitch = findViewById(R.id.cancelSwitch);
        cancelSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationReceiver.cancelReminder(MainActivity.this,NotificationReceiver.class);
            }
        });
        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "onCreate: permission ");
            requestPermissions(new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkNextWorkingDay() {
        int daysCount = 6;
        Calendar calendar = Calendar.getInstance();
        Calendar cal = calendar;
        Log.e(TAG, "WorkingDay1: " + cal.getTime());
        cal = checkWeekdays(cal);
        for (int i = 0; i < daysCount; i++) {
            if (cal.get((Calendar.DAY_OF_WEEK)) == Calendar.SATURDAY
                    || cal.get((Calendar.DAY_OF_WEEK)) == Calendar.SUNDAY) {
                daysCount++;
                Log.e(TAG, "skip Sat Sun: ");
            }
            cal.add(Calendar.DATE, 1);
        }
        if (cal.get((Calendar.DAY_OF_WEEK)) == Calendar.SATURDAY) {
            cal.add(Calendar.DATE, 1);
        }
        if (cal.get((Calendar.DAY_OF_WEEK)) == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, 1);
        }
        Log.e(TAG, "FinalDate: " + cal.getTime());

    }


    private Calendar checkWeekdays(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(calendar.getTime());
        Log.e(TAG, "TIME : " + time + "  :  " + calendar.getTime());
        String[] timeArray = time.split(":");
        if (Integer.parseInt(timeArray[0]) < 9) {
            calendar.set(Calendar.HOUR, 9);
        } else if (Integer.parseInt(timeArray[0]) >= 17
                || (Integer.parseInt(timeArray[0]) >= 17 && Integer.parseInt(timeArray[1]) > 30)) {
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR, 9);
            calendar.set(Calendar.AM_PM, 0);
        } else  if (Integer.parseInt(timeArray[0]) <= 17
                || (Integer.parseInt(timeArray[0]) <= 17 && Integer.parseInt(timeArray[1]) <= 30)){
            calendar.add(Calendar.DATE,1);
            Log.e(TAG, "DateADDED: "+calendar.getTime() );
        }
        return calendar;
    }
}