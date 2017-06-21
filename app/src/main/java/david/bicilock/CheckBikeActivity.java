package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckBikeActivity extends AppCompatActivity {

    private String serialNumber;
    private JSONArray jSONArray;
    private ReturnJSON returnJSON;
    private Bike bike;
    private EditText etSerialNumberCheckBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bike);

        etSerialNumberCheckBike = (EditText)findViewById(R.id.etSerialNumberCheckBike);
        returnJSON = new ReturnJSON();
    }

    public void checkSerialNumber(View view){
        if(isEmpty(etSerialNumberCheckBike)){
            Toast.makeText(this, R.string.enter_sn, Toast.LENGTH_SHORT).show();
        } else {
            serialNumber = etSerialNumberCheckBike.getText().toString();
            new CheckBike().execute();
        }
    }

    ///////Task for check serial number
    class CheckBike extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(CheckBikeActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM bikes WHERE SerialNumber='" + serialNumber + "'");

                jSONArray = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArray != null) {
                    return jSONArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                try {
                    JSONObject jsonObject = json.getJSONObject(0);
                    bike = new Bike();
                    bike.setStolen(jsonObject.getInt("Stolen"));
                    if (bike.stolen == 1) {
                        Toast.makeText(CheckBikeActivity.this, R.string.stolen_bike, Toast.LENGTH_SHORT).show();
                        confirmScreen();
                    } else {
                        Toast.makeText(CheckBikeActivity.this, R.string.not_stolen, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(CheckBikeActivity.this, R.string.not_database, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CheckBikeActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void confirmScreen(){
        Intent intent = new Intent (this, ConfirmBikeActivity.class);
        intent.putExtra("serialNumber", serialNumber);
        startActivity(intent);
        finish();
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public void cancel(View view){
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
