package david.bicilock;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class EditBikeActivity extends AppCompatActivity {

    private Bike bike;
    private EditText etSerialNumberEdit, etBrandEdit, etModelEdit, etColorEdit, etYearEdit, etDetailsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bike);
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

        etSerialNumberEdit = (EditText) findViewById(R.id.etSerialNumberEdit);
        etBrandEdit = (EditText) findViewById(R.id.etBrandEdit);
        etModelEdit = (EditText) findViewById(R.id.etModelEdit);
        etColorEdit = (EditText) findViewById(R.id.etColorEdit);
        etYearEdit = (EditText) findViewById(R.id.etYearEdit);
        etDetailsEdit = (EditText) findViewById(R.id.etDetailsEdit);

        getBike();
        prepareScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveBikeEdit) {

        }
        if (id == R.id.cancelBikeEdit) {
            finish();
            Toast.makeText(this, "Edición cancilada", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getBike() {
        bike = (Bike)getIntent().getSerializableExtra("bike");
    }

    protected void prepareScreen() {
        etSerialNumberEdit.setText(bike.getSerialNumber());
        etBrandEdit.setText(bike.getBrand());
        etModelEdit.setText(bike.getModel());
        etColorEdit.setText(bike.getColor());
        etYearEdit.setText(bike.getYear());
        etDetailsEdit.setText((bike.getDetails()));
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
