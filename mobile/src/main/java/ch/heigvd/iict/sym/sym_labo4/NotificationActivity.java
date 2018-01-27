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

        findViewById(R.id.pendingIntent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int notificationId = 001;

                // Simple notification
                Notification notification = new NotificationCompat.Builder(NotificationActivity.this)
                    .setContentIntent(createPendingIntent(1,"Notifications clicked"))
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Nouvelles notifications")
                    .setContentText("Cliquez ici !")
                    .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Issue the notification with notification manager.
                notificationManager.notify(notificationId, notification);
            }
        });

        findViewById(R.id.intentButtons).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int notificationId = 001;
                // Intent principal pour afficher la notification
                 PendingIntent pi = createPendingIntent(0, "New email");

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
                Notification notification  = new Notification.Builder(NotificationActivity.this)
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_menu_delete, "delete", createPendingIntent(0,"Email deleted"))
                        .addAction(android.R.drawable.ic_menu_save, "Save", createPendingIntent(1, "Email saved"))
                        .addAction(android.R.drawable.ic_menu_more, "Open", createPendingIntent(2, "Email opened"))
                        .setStyle(new Notification.BigTextStyle().bigText("You have received a mail from "+"test@gmail.com.\nPlease choose an action"))
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Issue the notification with notification manager.
                notificationManager.notify(notificationId, notification);
            }
        });

        findViewById(R.id.intentWearable).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int notificationId = 001;
                // Action only on wearable
                NotificationCompat.WearableExtender we = new NotificationCompat.WearableExtender()
                        .addAction(new NotificationCompat.Action(android.R.drawable.ic_menu_save, "Save", createPendingIntent(0, "Email saved")));

                // Action on phone and wearable
                NotificationCompat.Builder builder  = new NotificationCompat.Builder(NotificationActivity.this)
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setAutoCancel(true)
                        .extend(we);

                // Build
                Notification notification = builder.build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Issue the notification with notification manager.
                notificationManager.notify(notificationId, notification);
            }
        });
    }

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
