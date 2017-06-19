package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfirmBikeActivity extends AppCompatActivity {

    private EditText etSerialNumberConfirm, etBrandConfirm, etModelConfirm, etColorConfirm, etYearConfirm, etDetailsConfirm;
    private JSONArray jSONArrayBikes, jSONArrayPhotos;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private Bike bike;
    private String serialNumber, email;
    private Photo photo;
    private ArrayList<Photo> arrayPhotos;

    //recyclerview object
    private RecyclerView recyclerView;
    //adapter object
    private RecyclerView.Adapter adapter;
    //list to hold all the uploaded images
    private List<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_bike);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etSerialNumberConfirm = (EditText)findViewById(R.id.etSerialNumberConfirm);
        etBrandConfirm = (EditText)findViewById(R.id.etBrandConfirm);
        etModelConfirm = (EditText)findViewById(R.id.etModelConfirm);
        etColorConfirm = (EditText)findViewById(R.id.etColorConfirm);
        etYearConfirm = (EditText)findViewById(R.id.etYearConfirm);
        etDetailsConfirm = (EditText)findViewById(R.id.etDetailsConfirm);

        getSerialNumber();
        returnJSON = new ReturnJSON();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new BikeListTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_bike, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_yes) {
            showNotifyDialog();
        }
        if (id == R.id.confirm_no) {

        }
        return super.onOptionsItemSelected(item);
    }

    protected void getSerialNumber() {
        serialNumber = getIntent().getStringExtra("serialNumber");
    }

    protected void prepareScreen(){
        etSerialNumberConfirm.setText(bike.getSerialNumber());
        etBrandConfirm.setText(bike.getBrand());
        etModelConfirm.setText(bike.getModel());
        etColorConfirm.setText(bike.getColor());
        etYearConfirm.setText(bike.getYear());
        etDetailsConfirm.setText(bike.getDetails());
    }

    protected void showNotifyDialog(){
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(ConfirmBikeActivity.this);
        alertDialogBu.setTitle(R.string.stolen_bike);
        alertDialogBu.setMessage(R.string.notify_owner);
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialogBu.setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notifyScreen();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    protected void notifyScreen() {
        Intent intent = new Intent (this, NotifyActivity.class);
        intent.putExtra("serialNumber", serialNumber);
        startActivity(intent);
    }

    ///////Task for download user's bike data
    class BikeListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ConfirmBikeActivity.this);
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

                jSONArrayBikes = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArrayBikes != null) {
                    return jSONArrayBikes;
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
                long id;
                for (int i = 0; i < json.length(); i++) {
                    id = i;
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        email = jsonObject.getString("email");
                        bike = new Bike();
                        bike.setId(id);
                        bike.setSerialNumber(jsonObject.getString("SerialNumber"));
                        bike.setBrand(jsonObject.getString("Brand"));
                        bike.setModel(jsonObject.getString("Model"));
                        bike.setColor(jsonObject.getString("Color"));
                        bike.setYear(jsonObject.getString("Year"));
                        bike.setStolen(jsonObject.getInt("Stolen"));
                        bike.setDetails(jsonObject.getString("Details"));
                        bike.setUrlFav(jsonObject.getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    prepareScreen();
                    Toast.makeText(ConfirmBikeActivity.this, R.string.showing_bike, Toast.LENGTH_SHORT).show();
                    new DownloadPhotosTask().execute();
                }
            } else {
                Toast.makeText(ConfirmBikeActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////Task for download bike photos
    class DownloadPhotosTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ConfirmBikeActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM photos WHERE SerialNumber='" + serialNumber + "'");

                jSONArrayPhotos = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArrayPhotos != null) {
                    return jSONArrayPhotos;
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
                arrayPhotos = new ArrayList<Photo>();
                long position;
                for (int i = 0; i < json.length(); i++) {
                    position = i;
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        photo = new Photo();
                        photo.setPosition(position);
                        photo.setId(jsonObject.getString("id"));
                        photo.setSerialNumber(jsonObject.getString("SerialNumber"));
                        photo.setUrl(jsonObject.getString("url"));

                        arrayPhotos.add(photo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new AdapterRv(getApplicationContext(), arrayPhotos);
                    //adding adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(ConfirmBikeActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
