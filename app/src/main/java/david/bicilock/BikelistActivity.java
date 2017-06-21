package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BikelistActivity extends AppCompatActivity {

    private ListView lv;
    private static AdapterLv adapterLv;
    private String email, serialNumber;
    private JSONArray jSONArrayBikes, jSONArrayString;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private Bike bike;
    private int id;
    private ArrayList<Bike> arrayBikes;
    private ArrayList<String> arrayString;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getEmail();

        lv = (ListView)findViewById(android.R.id.list);
        lv.setLongClickable(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                bike = (Bike) lv.getAdapter().getItem(position);
                Intent intent = new Intent (getApplicationContext(), ShowBikeActivity.class);
                intent.putExtra("bike", bike);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                bike = (Bike)lv.getAdapter().getItem(index);
                serialNumber = bike.getSerialNumber();
                id = index;
                showConfirmDialog();
                return true;
            }
        });

        returnJSON = new ReturnJSON();
        new BikeListTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bike_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addBike) {
            Intent intent = new Intent (this, NewBikeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getEmail(){
        sp = getSharedPreferences("preferences", this.MODE_PRIVATE);
        email = sp.getString("email", "null");
    }

    ///////Task for download user's bikes
    class BikeListTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BikelistActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                //parametrosPost.put("ins_sql", "SELECT b.SerialNumber, b.Brand, b.Model, b.Color, b.Year, b.Stolen, b.Details, p.url, p.Favourite FROM bikes b LEFT JOIN photos p ON b.SerialNumber=p.SerialNumber AND email='" + email + "' AND p.Favourite = 1");
                //parametrosPost.put("ins_sql", "SELECT * FROM bikes WHERE email='" + email + "'");
                parametrosPost.put("ins_sql", "SELECT b.SerialNumber, b.Brand, b.Model, b.Color, b.Year, b.Stolen, b.Details, p.url, p.Favourite FROM bikes b LEFT JOIN photos p ON b.SerialNumber=p.SerialNumber AND email='" + email + "'");

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
                arrayBikes = new ArrayList<Bike>();
                long id;
                String previousSerial = "";
                for (int i = 0; i < json.length(); i++) {
                    id = i;
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
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

                        if (!previousSerial.equals(jsonObject.getString("SerialNumber"))) {
                            arrayBikes.add(bike);
                        }
                        previousSerial = jsonObject.getString("SerialNumber");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterLv = new AdapterLv(BikelistActivity.this, arrayBikes);
                    adapterLv.notifyDataSetChanged();
                    lv.setAdapter(adapterLv);

                    Toast.makeText(BikelistActivity.this, R.string.showing_garage, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BikelistActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////Task for remove a bike
    class DeleteBikeTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BikelistActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "DELETE FROM bikes WHERE SerialNumber='" + serialNumber + "'");

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
                    Toast.makeText(BikelistActivity.this, R.string.successfully_removed, Toast.LENGTH_SHORT).show();
                    arrayBikes.remove(id);
                    adapterLv.notifyDataSetChanged();
                }else{
                    Toast.makeText(BikelistActivity.this, R.string.error_removing, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BikelistActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////Task for delete all photos
    class DeleteAllPhotosTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BikelistActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM photos WHERE SerialNumber='" + serialNumber + "'");

                jSONArrayString = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArrayString != null) {
                    return jSONArrayString;
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
                arrayString = new ArrayList<String>();
                String idPhoto;
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        idPhoto = jsonObject.getString("id");
                        arrayString.add(idPhoto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                borrar();
            } else {}
        }
    }

    protected void showConfirmDialog(){
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(BikelistActivity.this);
        alertDialogBu.setTitle(R.string.remove_bike);
        alertDialogBu.setMessage(R.string.are_you_sure);
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialogBu.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAllPhotosTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    protected void borrar() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        for (int i = 0; i < arrayString.size(); i++) {
            StorageReference toDeleteFile = storageReference.child("images/" + serialNumber + "/" + arrayString.get(i));

            toDeleteFile.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }

        new DeleteBikeTask().execute();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }
}
