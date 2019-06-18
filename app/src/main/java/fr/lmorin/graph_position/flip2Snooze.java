package fr.lmorin.graph_position;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class flip2Snooze extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_flip2_snooze);
        setContentView(new BoussoleView(getApplicationContext()));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

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
