package david.bicilock;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SetStolenActivity extends AppCompatActivity {

    Bike bike;
    private TextView tvBrandSetStolen, tvModelSetStolen, tvSerialNumberSetStolen;
    private EditText etDetailsSetStolen;

    private String url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_stolen);
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

        tvBrandSetStolen = (TextView)findViewById(R.id.tvBrandSetStolen);
        tvModelSetStolen = (TextView)findViewById(R.id.tvModelSetStolen);
        tvSerialNumberSetStolen = (TextView)findViewById(R.id.tvSerialNumberSetStolen);
        etDetailsSetStolen = (EditText)findViewById(R.id.etDetailsSetStolen);

        getBike();
        showBikeData();

        url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
        returnJSON = new ReturnJSON();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_stolen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cancelNewBike) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getBike() {
        bike = (Bike)getIntent().getSerializableExtra("bike");
    }

    protected void showBikeData(){
        tvBrandSetStolen.setText(bike.getBrand());
        tvModelSetStolen.setText(bike.getModel());
        tvSerialNumberSetStolen.setText(bike.getSerialNumber());
    }

    public void setStolen(View view) {
        bike.setDetails(etDetailsSetStolen.getText().toString());
        new SetStolenTask().execute();
    }

    ///////Task para marcar una bici como robada
    class SetStolenTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SetStolenActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE bikes SET Stolen = 1, Details = '" + bike.getDetails() + "' WHERE SerialNumber = '" + bike.getSerialNumber() + "'");
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
                    Toast.makeText(SetStolenActivity.this, "Bicicleta Registrada",
                            Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(SetStolenActivity.this, "Error. No se ha podido registrar",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SetStolenActivity.this, "Error. No se ha podido registrar 2",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
