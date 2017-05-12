package david.bicilock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NewBikeActivity extends AppCompatActivity {

    private String url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
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

        url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
        returnJSON = new ReturnJSON();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveBike) {
            tomaDatos();
            new NewBikeTask().execute();
        }

        if (id == R.id.cancelNewBike) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void tomaDatos(){
        serialNumber = etSerialNumberNew.getText().toString();
        brand = etBrandNew.getText().toString();
        model = etModelNew.getText().toString();
        color = etColorNew.getText().toString();
        year = etYearNew.getText().toString();
    }

    ///////Task para registrar una nueva bici
    class NewBikeTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NewBikeActivity.this);
            pDialog.setMessage("Cargando...");
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
                jsonObject = returnJSON.sendDMLRequest(url_subida, parametrosPost);

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
                    Toast.makeText(NewBikeActivity.this, "Bicicleta Registrada",
                            Toast.LENGTH_LONG).show();
                    addPhotosScreen();
                }else{
                    Toast.makeText(NewBikeActivity.this, "Error. No se ha podido registrar",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(NewBikeActivity.this, "Error. No se ha podido registrar",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addPhotosScreen(){
        Intent intent = new Intent (this, UploadPhotosActivity.class);
        intent.putExtra("serialNumber", serialNumber);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

}
