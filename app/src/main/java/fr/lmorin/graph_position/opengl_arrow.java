package fr.lmorin.graph_position;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class opengl_arrow extends AppCompatActivity
        implements GLSurfaceView.Renderer {

    public static final String EXTRA_MESSAGE = "fr.lmorin.graph_position.launch_main_activity";
    private GLSurfaceView gLView;


    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    private float[] gravity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_arrow);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_back_toolbar);
        setSupportActionBar(myToolbar);

        gLView = findViewById(R.id.glSurface);
        gLView.setPreserveEGLContextOnPause(true);
        gLView.setEGLContextClientVersion(2);
        gLView.setRenderer(this);
        gLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[]{0.f, 0.0f, -9.81f};
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_back_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_back:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Context context = getApplicationContext();
                CharSequence text =     "You are back!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent intent = new Intent(this, MainActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gLView.onResume();
        mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gLView.onPause();
        mSensorManager.unregisterListener(mSensorEventListener, mAccelerometer);
    }




    //private Triangle mTriangle;
    private fleche mFleche;
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color

        GLES20.glClearColor(0.0f,
                            43.0f/255.0f,
                            54.0f/255.0f,1f);


        mFleche = new fleche(getApplicationContext());


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 4f, 12);
        // Since we requested our OpenGL thread to only render when dirty, we have to tell it to.
        //gl.requestRender();
        gLView.requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, -gravity[0], -gravity[1], -gravity[2],
                0f, 0.0f, 0.0f, 0f, 1.0f, 0.f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Draw shape
        mFleche.draw(vPMatrix);


    }




    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            throw new RuntimeException("Compilation failed : " + GLES20.glGetShaderInfoLog(shader) +"*");
        }
        return shader;
    }

    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Rien a faire en cas de changement de précision
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            final float alpha = 0.85f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] - (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] - (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] - (1 - alpha) * sensorEvent.values[2];

            gLView.requestRender();


        }
    };

}



