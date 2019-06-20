package fr.lmorin.graph_position;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import static java.lang.Math.sqrt;


public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;

    private XYPlot plot;
    private SimpleXYSeries seriesx = null;
    private SimpleXYSeries seriesy = null;
    private SimpleXYSeries seriesz = null;
    private Redrawer redrawer;
    private static final int HISTORY_SIZE = 1000;
    private float[] gravity ;

    public static final String EXTRA_MESSAGE = "fr.lmorin.graph_position.launch_flip_activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        plot = (XYPlot) findViewById(R.id.plot);

        gravity = new float[]{0f, 0f, 0f};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)

        seriesx = new SimpleXYSeries("g_x");
        seriesx.useImplicitXVals();
        seriesy = new SimpleXYSeries("g_y");
        seriesy.useImplicitXVals();
        seriesz = new SimpleXYSeries("g_z");
        seriesz.useImplicitXVals();

        plot.setRangeBoundaries(-12, 12, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        plot.addSeries(seriesx, new LineAndPointFormatter(
                Color.rgb(100, 100, 200), null, null, null) );
        plot.addSeries(seriesy, new LineAndPointFormatter(
                Color.rgb(100, 200, 100), null, null, null) );
        plot.addSeries(seriesz, new LineAndPointFormatter(
                Color.rgb(200, 100, 100), null, null, null) );





        redrawer = new Redrawer(plot,100, false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);




    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        redrawer.start();
    }

    @Override
    protected void onPause() {
        redrawer.pause();
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);

    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }



    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Rien a faire en cas de changement de précision
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            final float alpha = 0.95f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] - (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] - (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] - (1 - alpha) * sensorEvent.values[2];


            TextView Tx  = (TextView) findViewById(R.id.value_gx);
            String unité = " m/s<sup>2</sup>";
            Tx.setText(String.format("%.2f",gravity[0])  );
            TextView Ty  = (TextView) findViewById(R.id.value_gy);
            Ty.setText(Html.fromHtml( String.format("%.2f",gravity[1])  ));
            TextView Tz  = (TextView) findViewById(R.id.value_gz);
            Tz.setText(Html.fromHtml(String.format("%.2f",gravity[2]) ));
            if (seriesy.size() > HISTORY_SIZE) {
                seriesx.removeFirst();
                seriesy.removeFirst();
                seriesz.removeFirst();
            }

            seriesx.addLast(null, gravity[0]);
            seriesy.addLast(null, gravity[1]);
            seriesz.addLast(null, gravity[2]);

            TextView titre = findViewById(R.id.titre);
            float norm = (float) sqrt(gravity[0]*gravity[0]+gravity[1]*gravity[1]+gravity[2]*gravity[2]);
            titre.setText(Html.fromHtml(String.format("%.2f",norm) ));

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getApplicationContext();
        CharSequence text ;
        int duration = Toast.LENGTH_SHORT;
        Toast toast;
        Intent intent ;
        switch (item.getItemId()) {


            case R.id.action_favorite:
                    text = "Hello Arrow!";

                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    intent = new Intent(this, opengl_arrow.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);


                return true;



            case R.id.action_flip:
                text =     "We will flip !";

                toast = Toast.makeText(context, text, duration);
                toast.show();
                intent = new Intent(this, flip2Snooze.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




}
