package ch.heigvd.iict.sym.sym_labo4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.heigvd.iict.sym.wearcommon.Constants;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if(getIntent() != null)
            onNewIntent(getIntent());

        /* A IMPLEMENTER */
        findViewById(R.id.pendingIntent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationActivity.this);
                builder.setContentIntent(createPendingIntent(1,"Notifications clicked"));
                builder.setSmallIcon(android.R.drawable.ic_dialog_info);
                builder.setContentTitle("Nouvelles notifications");
                builder.setContentText("Cliquez ici !");

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, builder.build());
            }
        });

        findViewById(R.id.intentButtons).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                // Intent principal pour afficher la notification
                Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(NotificationActivity.this, (int) System.currentTimeMillis(), intent, 0);

                // Build et ajouter les boutons de choix
                // Pour une raison inconnu avec un samsung S5 mini, windows 10 et android
                // studio 3.0.1 il faut débrancher le natel de l'usb pour avoir les boutons
                // activer dans la notification, sinon la notification apaprait comme une simple
                // notification.
                // https://stackoverflow.com/questions/18249871/android-notification-buttons-not-showing-up
                // Ici la réponse :
                // " Let me tell you something which is really awkward. If you have anything in your
                // Ongoing Notification, You wont see the buttons. Typically it happens when you
                // have phone connected to PC via USB. Hope this solves your problem"
                Notification n  = new Notification.Builder(NotificationActivity.this)
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setWhen(0)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_menu_delete, "delete", createPendingIntent(0,"Email deleted"))
                        .addAction(android.R.drawable.ic_menu_save, "Save", createPendingIntent(0, "Email saved"))
                        .addAction(android.R.drawable.ic_menu_more, "Open", createPendingIntent(0, "Email opened"))
                        .setStyle(new Notification.BigTextStyle().bigText("You have received a mail from "+"test@gmail.com.\nPlease choose an action"))
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
            }
        });

        findViewById(R.id.intentWearable).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int notificationId = 001;
                // The channel ID of the notification.
                String id = "my_channel_01";
                // Build intent for notification content
              PendingIntent pi = createPendingIntent(1, "Notification only wearable");

            // Notification channel ID is ignored for Android 7.1.1
            // (API level 25) and lower.
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(NotificationActivity.this, id)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle("Only wearable")
                                .setContentText("Hello")
                                .setContentIntent(pi);

                // Get an instance of the NotificationManager service
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(NotificationActivity.this);

            // Issue the notification with notification manager.
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        });


    }

    private PendingIntent createPendingIntentNotBuild(int code, String message){
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setAction(Constants.MY_PENDING_INTENT_ACTION);
        intent.putExtra("msg", message);

        return PendingIntent.getService(this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /*
     *  Code fourni pour les PendingIntent
     */

    /*
     *  Method called by system when a new Intent is received
     *  Display a toast with a message if the Intent is generated by
     *  createPendingIntent method.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent == null) return;
        if(Constants.MY_PENDING_INTENT_ACTION.equals(intent.getAction()))
            Toast.makeText(this, "" + intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
    }

    /**
     * Method used to create a PendingIntent with the specified message
     * The intent will start a new activity Instance or bring to front an existing one.
     * See parentActivityName and launchMode options in Manifest
     * See https://developer.android.com/training/notify-user/navigation.html for TaskStackBuilder
     * @param requestCode The request code
     * @param message The message
     * @return The pending Intent
     */
    private PendingIntent createPendingIntent(int requestCode, String message) {
        Intent myIntent = new Intent(NotificationActivity.this, NotificationActivity.class);
        myIntent.setAction(Constants.MY_PENDING_INTENT_ACTION);
        myIntent.putExtra("msg", message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(myIntent);

        return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
