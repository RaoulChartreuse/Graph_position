package fr.lmorin.graph_position;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class flip2Snooze extends AppCompatActivity {

    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    private BoussoleView bView_xy, bView_xz, bView_yz;
    private float[] gravity ;
    private boolean started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip2_snooze);
        //setContentView(new BoussoleView(getApplicationContext()));
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bView_xy = findViewById(R.id.boussole_xy);
        bView_xz = findViewById(R.id.boussole_xz);
        bView_yz = findViewById(R.id.boussole_yz);

        gravity = new float[]{0f, 0f, 0f};

        started =false;

    }


    //Linked by the xml layout
    public void start_sequence(View v) {
        if (!started){
            bView_xy.enableXlines();
            bView_xz.enableXlines();
            bView_yz.enableXlines();
            started = true;
        }
        else{
            bView_xy.disableXline();
            bView_xz.disableXline();
            bView_yz.disableXline();
            started = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);

    }

    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Rien a faire en cas de changement de précision
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            final float alpha = 0.98f;
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] - (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] - (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] - (1 - alpha) * sensorEvent.values[2];

            bView_xy.setGrav(gravity[0], gravity[1]);
            bView_xz.setGrav(gravity[0], gravity[2]);
            bView_yz.setGrav(gravity[1], gravity[2]);
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
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                text =     "Hello toast!";


                toast = Toast.makeText(context, text, duration);
                toast.show();
                intent = new Intent(this, opengl_arrow.class);
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
