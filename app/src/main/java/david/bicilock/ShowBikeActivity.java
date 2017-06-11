package david.bicilock;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ShowBikeActivity extends AppCompatActivity {

    private Bike bike;
    private EditText etSerialNumberShow, etBrandShow, etModelShow, etColorShow, etYearShow, etDetailsShow;
    private CheckBox checkBoxStolenShow;
    private Button btnSetStolen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bike);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        etSerialNumberShow = (EditText)findViewById(R.id.etSerialNumberShow);
        etBrandShow = (EditText)findViewById(R.id.etBrandShow);
        etModelShow = (EditText)findViewById(R.id.etModelShow);
        etColorShow = (EditText)findViewById(R.id.etColorShow);
        etYearShow = (EditText)findViewById(R.id.etYearShow);
        etDetailsShow = (EditText)findViewById(R.id.etDetailsShow);
        checkBoxStolenShow = (CheckBox)findViewById(R.id.checkBoxStolenShow);
        btnSetStolen = (Button)findViewById(R.id.buttonSetStolenShowBike);

        getBike();
        prepareScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editBike) {
            editBikeScreen();
            finish();
        }

        if (id == R.id.deleteBike) {

        }

        return super.onOptionsItemSelected(item);
    }

    protected void getBike() {
        bike = (Bike)getIntent().getSerializableExtra("bike");
    }

    protected void prepareScreen(){
        etSerialNumberShow.setText(bike.getSerialNumber());
        etBrandShow.setText(bike.getBrand());
        etModelShow.setText(bike.getModel());
        etColorShow.setText(bike.getColor());
        etYearShow.setText(bike.getYear());
        etDetailsShow.setText(bike.getDetails());
        if(bike.getStolen() == 1) {
            checkBoxStolenShow.setChecked(true);
        }
        checkBoxStolenShow.setClickable(false);
    }

    public void showPhotos(View view) {
        Intent intent = new Intent (this, ShowPhotosActivity.class);
        intent.putExtra("serialNumber", etSerialNumberShow.getText().toString());
        startActivity(intent);
    }

    public void setStolenScreen(View view) {
        Intent intent = new Intent (getApplicationContext(), SetStolenActivity.class);
        intent.putExtra("bike", bike);
        startActivity(intent);
    }

    protected void editBikeScreen(){
        Intent intent = new Intent (getApplicationContext(), EditBikeActivity.class);
        intent.putExtra("bike", bike);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("bike", bike);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bike = (Bike) savedInstanceState.getSerializable("bike");
    }
}
