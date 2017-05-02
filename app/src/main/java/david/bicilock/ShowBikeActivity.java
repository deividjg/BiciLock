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
import android.widget.EditText;

public class ShowBikeActivity extends AppCompatActivity {

    private Bike bike;
    private EditText etSerialNumberShow, etBrandShow, etModelShow, etColorShow, etYearShow;

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

        getBike();
        showBikeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.editBike) {

        }

        if (id == R.id.deleteBike) {

        }

        return super.onOptionsItemSelected(item);
    }

    protected void getBike() {
        bike = (Bike)getIntent().getSerializableExtra("bike");
    }

    protected void showBikeData(){
        etSerialNumberShow.setText(bike.getSerialNumber());
        etBrandShow.setText(bike.getBrand());
        etModelShow.setText(bike.getModel());
        etColorShow.setText(bike.color);
        etYearShow.setText(bike.year);
    }

    public void showPhotos(View view) {
        Intent intent = new Intent (this, ShowImagesActivity.class);
        intent.putExtra("serialNumber", etSerialNumberShow.getText().toString());
        startActivity(intent);
    }

}
