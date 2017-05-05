package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BikelistActivity extends AppCompatActivity {

    ListView lv;
    static Adapter a;

    private String url_consulta, url_borrado, email, numSerie;
    private JSONArray jSONArrayBikes;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private Bike bike;
    private int id;
    private ArrayList<Bike> arrayBikes;

    SharedPreferences sp;

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
                Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();
                Intent intent = new Intent (getApplicationContext(), ShowBikeActivity.class);
                intent.putExtra("bike", bike);
                startActivity(intent);
            }


        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                bike = (Bike)lv.getAdapter().getItem(index);
                numSerie = bike.getSerialNumber();
                id = index;
                showConfirmDialog();
                return true;
            }
        });

        url_consulta = "http://iesayala.ddns.net/deividjg/php.php";
        url_borrado = "http://iesayala.ddns.net/deividjg/prueba.php";
        returnJSON = new ReturnJSON();
        new BikeListTask().execute();

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

    ///////Task para descargar las bicicletas del usuario
    class BikeListTask extends AsyncTask<String, String, JSONArray> {
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
                parametrosPost.put("ins_sql", "Select * from bikes where email='" + email + "'");

                jSONArrayBikes = returnJSON.sendRequest(url_consulta, parametrosPost);

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
                        bike.setDetails("Details");
                        arrayBikes.add(bike);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    a = new Adapter(BikelistActivity.this, arrayBikes);
                    a.notifyDataSetChanged();
                    lv.setAdapter(a);

                    Toast.makeText(BikelistActivity.this, "Carga correcta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BikelistActivity.this, "Error en la carga del garaje", Toast.LENGTH_LONG).show();
            }
        }
    }

    ///////Task para eliminar una bicicleta
    class DeleteBikeTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BikelistActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "DELETE FROM bikes WHERE SerialNumber='" + numSerie + "'");

                jsonObject = returnJSON.sendDMLRequest(url_borrado, parametrosPost);

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
                    Toast.makeText(BikelistActivity.this, "Registro borrado", Toast.LENGTH_LONG).show();
                    arrayBikes.remove(id);
                    a.notifyDataSetChanged();
                }else{
                    Toast.makeText(BikelistActivity.this, "Error al borrar", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(BikelistActivity.this, "JSON Array nulo", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void showConfirmDialog(){
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(BikelistActivity.this);
        alertDialogBu.setTitle("Eliminar bicicleta");
        alertDialogBu.setMessage("¿Estás seguro?");
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Boton Rechazar pulsado", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBu.setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteBikeTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }
}
