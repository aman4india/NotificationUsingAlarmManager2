package com.aman4india.notificationspike;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {
    public static final int DAILY_REMINDER_REQUEST_CODE = 100;
    private static final String TAG = "NotificationReceiver";

    public static void setReminder(Context context, Class<?> cls, int hour, int min) {
        Log.e(TAG, "setReminder: " + hour + " : " + min + " " + cls.getSimpleName());
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
//        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
//        setcalendar.set(Calendar.MINUTE, min);
//        setcalendar.set(Calendar.SECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat hsdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String date = sdf.format(new Date());
        String time = hsdf.format(new Date());
        String[] dateArray = date.split("-");
        String[] timeArray = time.split(":");
        Log.e(TAG, "setAlarm: " + date + "  " + time + "  " + (Integer.parseInt(timeArray[1])));
        Log.e(TAG, "Date: " + dateArray[0] + " " + dateArray[1] + " " + dateArray[2]);
        Log.e(TAG, "Time: " + timeArray[0] + " " + timeArray[1]);
        setcalendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        setcalendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1);
        setcalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
        setcalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        setcalendar.set(Calendar.MINUTE, (Integer.parseInt(timeArray[1]) + 1));
//        setcalendar.set(Calendar.WEEK_OF_YEAR,Calendar.MONTH,28,Integer.parseInt(timeArray[0]),Integer.parseInt(timeArray[1])+1);

        // cancel already scheduled reminders
        cancelReminder(context, cls);

        if (setcalendar.before(calendar)) {
            calendar.add(Calendar.DATE, 1);
            Log.e(TAG, "before: " + calendar.getTimeInMillis());
        }

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.e(TAG, "setReminder:1 " + setcalendar.getTime() + " " + setcalendar.getTimeInMillis());
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
        setcalendar.set(Calendar.MINUTE, (Integer.parseInt(timeArray[1]) + 2));

        Log.e(TAG, "setReminder:2 " + setcalendar.getTime() + " " + setcalendar.getTimeInMillis());
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 101, intent1, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent1);

        setcalendar.set(Calendar.MINUTE, (Integer.parseInt(timeArray[1]) + 3));
        Log.e(TAG, "setReminder:3 " + setcalendar.getTime() + " " + setcalendar.getTimeInMillis());
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 102, intent1, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent2);

    }

    public static void cancelReminder(Context context, Class<?> cls) {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: " + intent.getStringExtra("emailAddress"));
        MainActivity.createNotification(context);
    }



}
