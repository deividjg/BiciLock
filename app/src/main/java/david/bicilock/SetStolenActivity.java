package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SetStolenActivity extends AppCompatActivity {

    Bike bike;
    private TextView tvBrandSetStolen, tvModelSetStolen, tvSerialNumberSetStolen;
    private CheckBox checkBoxStolenShow;
    private EditText etDetailsSetStolen;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_stolen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvBrandSetStolen = (TextView)findViewById(R.id.tvBrandSetStolen);
        tvModelSetStolen = (TextView)findViewById(R.id.tvModelSetStolen);
        tvSerialNumberSetStolen = (TextView)findViewById(R.id.tvSerialNumberSetStolen);
        checkBoxStolenShow = (CheckBox)findViewById(R.id.checkBoxStolenSetStolen);
        etDetailsSetStolen = (EditText)findViewById(R.id.etDetailsSetStolen);

        getBike();
        showBikeData();

        returnJSON = new ReturnJSON();

        /*checkBoxStolenShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBoxStolenShow.isChecked()) {
                        etDetailsSetStolen.setVisibility(View.VISIBLE);
                    } else {
                        etDetailsSetStolen.setVisibility(View.INVISIBLE);
                    }
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_stolen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_lv clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify adapterLv parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cancelSetStolen) {
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
        etDetailsSetStolen.setText(bike.getDetails());
        if (bike.getStolen() == 1) {
            checkBoxStolenShow.setChecked(true);
        }
    }

    public void setState(View view) {
        showConfirmDialog();
    }

    protected void showConfirmDialog(){
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(SetStolenActivity.this);
        alertDialogBu.setTitle(R.string.modify_state);
        alertDialogBu.setMessage(R.string.are_you_sure);
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        alertDialogBu.setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                bike.setDetails(etDetailsSetStolen.getText().toString());
                new SetStolenTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    ///////Task to change state
    class SetStolenTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SetStolenActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();

                if(bike.getStolen() == 0){
                    parametrosPost.put("ins_sql", "UPDATE bikes SET Stolen = 1, Details = '" + bike.getDetails() + "' WHERE SerialNumber = '" + bike.getSerialNumber() + "'");
                } else {
                    parametrosPost.put("ins_sql", "UPDATE bikes SET Stolen = 0, Details = '" + bike.getDetails() + "' WHERE SerialNumber = '" + bike.getSerialNumber() + "'");
                }

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
                    Toast.makeText(SetStolenActivity.this, R.string.state_modified_ok, Toast.LENGTH_LONG).show();
                    refreshBike();
                    Intent intent = new Intent (getApplicationContext(), ShowBikeActivity.class);
                    intent.putExtra("bike", bike);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SetStolenActivity.this, R.string.state_modified_error,
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SetStolenActivity.this, R.string.charging_error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void refreshBike() {
        if (checkBoxStolenShow.isChecked()) {
            bike.setStolen(1);
        } else {
            bike.setStolen(0);
        }
        bike.setDetails(etDetailsSetStolen.getText().toString());
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
