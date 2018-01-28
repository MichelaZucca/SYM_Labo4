package ch.heigvd.iict.sym.sym_labo4;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;

import static ch.heigvd.iict.sym.wearcommon.Constants.BACKGROUND_COLORS;
import static ch.heigvd.iict.sym.wearcommon.Constants.MY_PENDING_INTENT_ACTION;

public class WearSynchronizedActivity extends AppCompatActivity implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener  {

    private static final String TAG = WearSynchronizedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearsynchronized);

        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
        Wearable.getMessageClient(this).addListener(this);
        Wearable.getCapabilityClient(this)
                .addListener(
                        this, BACKGROUND_COLORS);

        System.out.println("klamdkamsdlkalsmdlkasm");
    }

    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("Received colors...");

        if (messageEvent.getPath().equals(BACKGROUND_COLORS)) {
            byte[] colors = messageEvent.getData();
            ByteBuffer colorsBuffer = ByteBuffer.wrap(colors);

            int r = colorsBuffer.getInt();
            int g = colorsBuffer.getInt();
            int b = colorsBuffer.getInt();

            updateColor(r, g, b);
        }
    }

    /*
     *  Code utilitaire fourni
     */

    /**
     * Method used to update the background color of the activity
     * @param r The red composant (0...255)
     * @param g The green composant (0...255)
     * @param b The blue composant (0...255)
     */
    private void updateColor(int r, int g, int b) {
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.argb(255, r,g,b));
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        System.out.println("LOLOL");
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        System.out.println("LOLOLOL");
    }
}
