package ch.heigvd.iict.sym.sym_labo4;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;

import com.bozapro.circularsliderrange.CircularSliderRange;
import com.bozapro.circularsliderrange.ThumbEvent;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import ch.heigvd.iict.sym.sym_labo4.widgets.CircularSliderRangeFixed;

import static ch.heigvd.iict.sym.wearcommon.Constants.BACKGROUND_COLORS;
import static ch.heigvd.iict.sym.wearcommon.Constants.MY_PENDING_INTENT_ACTION;

public class MainActivityWear extends WearableActivity {

    private static final String TAG = MainActivityWear.class.getSimpleName();

    private BoxInsetLayout mContainerView           = null;

    private CircularSliderRangeFixed redSlider      = null;
    private CircularSliderRangeFixed greenSlider    = null;
    private CircularSliderRangeFixed blueSlider     = null;

    private double startAngleRed    =     0;
    private double startAngleGreen  =     0;
    private double startAngleBlue   =     0;
    private double endAngleRed      = 90+30;
    private double endAngleGreen    = 90+60;
    private double endAngleBlue     = 90+90;

    private String backgroundColorNodeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        setAmbientEnabled();

        //link to GUI
        this.mContainerView = findViewById(R.id.container);
        this.redSlider      = findViewById(R.id.circular_red);
        this.greenSlider    = findViewById(R.id.circular_green);
        this.blueSlider     = findViewById(R.id.circular_blue);

        //events
        this.redSlider.setOnSliderRangeMovedListener(new CircularSliderRange.OnSliderRangeMovedListener() {
            @Override public void onStartSliderMoved(double pos) { /* fixed */ }

            @Override public void onEndSliderMoved(double pos) {
                endAngleRed = 90 + pos;
            }

            @Override public void onStartSliderEvent(ThumbEvent event) { }

            @Override
            public void onEndSliderEvent(ThumbEvent event) {
                updateColor();
            }
        });

        this.greenSlider.setOnSliderRangeMovedListener(new CircularSliderRange.OnSliderRangeMovedListener() {
            @Override public void onStartSliderMoved(double pos) { /* fixed */ }

            @Override public void onEndSliderMoved(double pos) {
                endAngleGreen = 90 + pos;
            }

            @Override public void onStartSliderEvent(ThumbEvent event) { }

            @Override
            public void onEndSliderEvent(ThumbEvent event) {
                updateColor();
            }
        });

        this.blueSlider.setOnSliderRangeMovedListener(new CircularSliderRange.OnSliderRangeMovedListener() {
            @Override public void onStartSliderMoved(double pos) { /* fixed */ }

            @Override public void onEndSliderMoved(double pos) {
                endAngleBlue = 90 + pos;
            }

            @Override public void onStartSliderEvent(ThumbEvent event) { }

            @Override
            public void onEndSliderEvent(ThumbEvent event) {
                updateColor();
            }
        });

        updateColor();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setupBackgroundColor();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void setupBackgroundColor() throws ExecutionException, InterruptedException {
        CapabilityInfo capabilityInfo = Tasks.await(
                Wearable.getCapabilityClient(this).getCapability(
                        BACKGROUND_COLORS, CapabilityClient.FILTER_REACHABLE));
        // capabilityInfo has the reachable nodes with the transcription capability
        updateTranscriptionCapability(capabilityInfo);

        // Declare an OnCapabilityChangedListener
        CapabilityClient.OnCapabilityChangedListener capabilityListener =
                new CapabilityClient.OnCapabilityChangedListener() {
            @Override
            public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
                System.out.println("Something changed.");
                updateTranscriptionCapability(capabilityInfo);
            }
        };

        // Register
        Wearable.getCapabilityClient(this).addListener(
                capabilityListener,
                BACKGROUND_COLORS);
    }

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        backgroundColorNodeId = pickBestNodeId(connectedNodes);

    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    /**
     * Request the background change on the smartphone.
     * @param colors RGB in an array of 12 bytes, (4 bytes for each of rgb colors)
     */
    private void requestBackgroundColor(byte[] colors) {

        System.out.println("Sending the colors to the user.");
        if (backgroundColorNodeId != null) {
            Task<Integer> sendTask =
                    Wearable.getMessageClient(this).sendMessage(
                            backgroundColorNodeId, BACKGROUND_COLORS, colors);

            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            //sendTask.addOnSuccessListener(...);
            //sendTask.addOnFailureListener(...);
        } else {
            // Unable to retrieve node with transcription capability
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
        } else {
            mContainerView.setBackground(null);
        }
    }

    /**
     *  Method called when a slider stops moving
     *  Color RGB composants are computed
     *  You need to send them to the smartphone application using DataLayer API
     */
    private void updateColor() {
        int r = (int) Math.round(255 * ((endAngleRed   - startAngleRed)   % 360) / 360.0);
        int g = (int) Math.round(255 * ((endAngleGreen - startAngleGreen) % 360) / 360.0);
        int b = (int) Math.round(255 * ((endAngleBlue  - startAngleBlue)  % 360) / 360.0);

        ByteBuffer colorsBuffer = ByteBuffer.allocate(12);
        colorsBuffer.putInt(r);
        colorsBuffer.putInt(g);
        colorsBuffer.putInt(b);

        requestBackgroundColor(colorsBuffer.array());
    }

}
