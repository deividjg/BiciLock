package david.bicilock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    Button buttonLogout, buttonRegister, buttonModifyPersonalData, buttonManageGarage, btnLogin;
    TextView tvEmail;

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
                Intent intent = new Intent(getApplicationContext(), UploadPhotosActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        tvEmail = (TextView) findViewById(R.id.tvEmailMain);
        buttonModifyPersonalData = (Button) findViewById(R.id.buttonModifyPersonalData);
        buttonManageGarage = (Button) findViewById(R.id.btnManageGarage);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        if (alreadyLogged()) {
            tvEmail.setText(sp.getString("email", "Invitado"));
            buttonRegister.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);

        } else {
            buttonLogout.setVisibility(View.INVISIBLE);
            buttonManageGarage.setVisibility(View.INVISIBLE);
            buttonModifyPersonalData.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.salir) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerScreen(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginScreen(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void bikeListScreen(View view) {
        Intent intent = new Intent(this, BikelistActivity.class);
        startActivity(intent);
    }

    public void checkBikeScreen(View view) {
        Intent intent = new Intent(this, CheckBikeActivity.class);
        startActivity(intent);
    }

    public boolean alreadyLogged() {
        sp = getSharedPreferences("preferences", this.MODE_PRIVATE);
        return sp.getBoolean("logged", false);
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("logged", false);
        editor.commit();
        recreate();
    }
}
