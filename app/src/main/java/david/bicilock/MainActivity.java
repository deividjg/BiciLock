package david.bicilock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    Button buttonLogout, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), UploadPhotosActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout = (Button)findViewById(R.id.buttonLogout);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);

        if(alreadyLogged()){
            buttonLogout.setVisibility(View.INVISIBLE);
            buttonRegister.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.salir) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void pantallaRegistro(View view){
        Intent intent = new Intent (this, RegisterActivity.class);
        startActivity(intent);
    }

    public void pantallaLogin(View view){
        if(alreadyLogged()){
            Intent intent = new Intent (this, BikelistActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent (this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void pantallaComprobar(View view){
        Intent intent = new Intent (this, CheckBikeActivity.class);
        startActivity(intent);
    }

    public void newBikeScreen(){
        Intent intent = new Intent (this, NewBikeActivity.class);
        startActivity(intent);
    }

    public boolean alreadyLogged() {
        sp = getSharedPreferences("preferences", this.MODE_PRIVATE);
        return sp.getBoolean("logged", false);
    }
}
