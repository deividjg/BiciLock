package david.bicilock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditBikeActivity extends AppCompatActivity {

    private Bike bike;
    private EditText etSerialNumberEdit, etBrandEdit, etModelEdit, etColorEdit, etYearEdit;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;

    private String brand, model, color, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bike);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        returnJSON = new ReturnJSON();

        etSerialNumberEdit = (EditText) findViewById(R.id.etSerialNumberEdit);
        etBrandEdit = (EditText) findViewById(R.id.etBrandEdit);
        etModelEdit = (EditText) findViewById(R.id.etModelEdit);
        etColorEdit = (EditText) findViewById(R.id.etColorEdit);
        etYearEdit = (EditText) findViewById(R.id.etYearEdit);

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
            if(isEmpty(etSerialNumberEdit) || isEmpty(etBrandEdit) || isEmpty(etModelEdit) || isEmpty(etColorEdit) || isEmpty(etYearEdit)) {
                Toast.makeText(this, R.string.complete_fields, Toast.LENGTH_SHORT).show();
            } else {
                takeNewData();
                new EditBikeTask().execute();
            }
        }
        if (id == R.id.cancelBikeEdit) {
            Toast.makeText(this, R.string.cancel_edition, Toast.LENGTH_SHORT).show();
            finish();
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
    }

    ///////Task for update a bike
    class EditBikeTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditBikeActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE bikes SET Brand='" + brand + "', Model='" + model + "', Color='" + color + "', Year=" + year + " WHERE SerialNumber='" + bike.getSerialNumber() + "'");
                jsonObject = returnJSON.sendDMLRequest(Parameters.URL_UPLOAD, parametrosPost);

                if (jsonObject != null) {
                    return jsonObject;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                try {
                    add = json.getInt("added");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(add!=0){
                    Toast.makeText(EditBikeActivity.this, R.string.bike_updated,
                            Toast.LENGTH_SHORT).show();
                    showBikeScreen();
                }else{
                    Toast.makeText(EditBikeActivity.this, R.string.error_updating,
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(EditBikeActivity.this, R.string.charging_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showBikeScreen(){
        bike.setBrand(brand);
        bike.setModel(model);
        bike.setColor(color);
        bike.setYear(year);
        Intent intent = new Intent (this, ShowBikeActivity.class);
        intent.putExtra("bike", bike);
        startActivity(intent);
        finish();
    }

    protected void takeNewData(){
        brand = etBrandEdit.getText().toString();
        model = etModelEdit.getText().toString();
        color = etColorEdit.getText().toString();
        year = etYearEdit.getText().toString();
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
