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

public class NewBikeActivity extends AppCompatActivity {

    private String url_upload;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private EditText etSerialNumberNew, etBrandNew, etModelNew, etColorNew, etYearNew;
    private String serialNumber, brand, model, color, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bike);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etSerialNumberNew = (EditText)findViewById(R.id.etSerialNumberNew);
        etBrandNew = (EditText)findViewById(R.id.etBrandNew);
        etModelNew = (EditText)findViewById(R.id.etModelNew);
        etColorNew = (EditText)findViewById(R.id.etColorNew);
        etYearNew = (EditText)findViewById(R.id.etYearNew);

        url_upload = "http://iesayala.ddns.net/deividjg/php2.php";
        returnJSON = new ReturnJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_lv clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify adapterLv parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveBike) {
            if(isEmpty(etSerialNumberNew) || isEmpty(etBrandNew) || isEmpty(etModelNew) || isEmpty(etColorNew) || isEmpty(etYearNew)) {
                Toast.makeText(this, R.string.complete_fields, Toast.LENGTH_SHORT).show();
            }
            else {
                dataCollect();
                new NewBikeTask().execute();
            }
        }

        if (id == R.id.cancelNewBike) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void dataCollect(){
        serialNumber = etSerialNumberNew.getText().toString();
        brand = etBrandNew.getText().toString();
        model = etModelNew.getText().toString();
        color = etColorNew.getText().toString();
        year = etYearNew.getText().toString();
    }

    ///////Task to registerUser a new bike
    class NewBikeTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NewBikeActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                //parametrosPost.put("ins_sql", "INSERT INTO 'bikes'('SerialNumber', 'Brand', 'Model', 'Color', 'Year', 'email') VALUES ('" + serialNumber + "', '" + brand + "', '" + model + "', '" + color + "', '" + year + "', 'deividjg@gmail.com)");
                parametrosPost.put("ins_sql", "INSERT INTO bikes VALUES ('" + serialNumber + "', '" + brand + "', '" + model + "', '" + color + "', '" + year + "', 0, 'detalles', 'deividjg@gmail.com')");
                jsonObject = returnJSON.sendDMLRequest(url_upload, parametrosPost);

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
                    Toast.makeText(NewBikeActivity.this, R.string.new_bike_ok,
                            Toast.LENGTH_LONG).show();
                    addPhotosScreen();
                }else{
                    Toast.makeText(NewBikeActivity.this, R.string.new_bike_error,
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(NewBikeActivity.this, R.string.charging_error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addPhotosScreen(){
        Intent intent = new Intent (this, UploadPhotosActivity.class);
        intent.putExtra("serialNumber", serialNumber);
        startActivity(intent);
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed(){
        finish();
    }

}
