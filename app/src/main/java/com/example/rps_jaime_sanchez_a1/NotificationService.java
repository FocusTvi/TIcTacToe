package com.example.rps_jaime_sanchez_a1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    private int counter;

    static final String CHANNEL_ID = "1";


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        //Step 1: we must create a notification channel to show the information
        createNotificacionChannel();

        counter = 0;

        final Timer timer = new Timer(true);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                counter++;

                try {
                    final Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }


                    //Step2: Constructing notification with icon, title and description
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_app_background)
                            .setContentTitle("RPS Game")
                            .setContentText("Hey, keep playing!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)//notification dissapears after click on it from the notification bar
                            .setContentIntent(pendingIntent);

                    //Step3: Showing the notification
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(0, builder.build());


                    //stop the service and cancel the timer and show the toast
                    timer.cancel();
                    stopSelf();

                } catch (Exception e) {
                    System.out.println("--------------------------------------------------------------------------------------------------");
                    e.printStackTrace();
                }


            }
        }, 10000, 2000);


        super.onCreate();
    }


    void createNotificacionChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Channel", NotificationManager.IMPORTANCE_DEFAULT);

            //Register our channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}