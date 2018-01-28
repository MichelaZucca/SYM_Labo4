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
import java.util.HashSet;
import java.util.List;
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

    }
    /**
     * Request the background change on the smartphone.
     * @param colors RGB in an array of 12 bytes, (4 bytes for each of rgb colors)
     */
    private void requestBackgroundColor(byte[] colors) {

        HashSet<String> results = new HashSet<String>();
        try {
            List<Node> nodes =
                    Tasks.await(Wearable.getNodeClient(this).getConnectedNodes());

            for (Node n : nodes) {
                System.out.println("Sending the colors to the user.");
                if (n != null) {
                    Task<Integer> sendTask =
                            Wearable.getMessageClient(this).sendMessage(
                                    n.getId(), BACKGROUND_COLORS, colors);

                    // You can add success and/or failure listeners,
                    // Or you can call Tasks.await() and catch ExecutionException
                    //sendTask.addOnSuccessListener(...);
                    //sendTask.addOnFailureListener(...);
                } else {
                    System.out.println("Aucun device disponible");
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        final ByteBuffer colorsBuffer = ByteBuffer.allocate(12);
        colorsBuffer.putInt(r);
        colorsBuffer.putInt(g);
        colorsBuffer.putInt(b);


        new Thread(new Runnable() {
            @Override
            public void run() {
                requestBackgroundColor(colorsBuffer.array());
            }
        }).start();
    }

}
