package david.bicilock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class bikelistActivity extends AppCompatActivity {

    ListView lv;
    static Adapter a;

    private String url_consulta, email;
    private JSONArray jSONArray;
    private ReturnJSON returnJSON;
    private Bike bike;
    private ArrayList<Bike> arrayBikes;
    ArrayList<HashMap<String, String>> bikeList;

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
                Bike bike = (Bike) lv.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent (getApplicationContext(), EditarActivity.class);
                intent.putExtra("idContacto", contacto.getId());
                startActivity(intent);*/
            }


        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                Toast.makeText(getApplicationContext(), "largoooo" + index, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        url_consulta = "http://iesayala.ddns.net/deividjg/php.php";
        returnJSON = new ReturnJSON();
        new CheckLogin().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    protected void getEmail(){
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(getApplicationContext(), "Error recibiendo e-mail", Toast.LENGTH_LONG).show();
        } else {
            email = extras.getString("email");
        }
    }

    ///////Task para comprobar conexcion de usuario
    class CheckLogin extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(bikelistActivity.this);
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

                jSONArray = returnJSON.sendRequest(url_consulta, parametrosPost);

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
                        bike.setStolen(jsonObject.getString("Stolen"));
                        bike.setDetails("Details");
                        arrayBikes.add(bike);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    a = new Adapter(bikelistActivity.this, arrayBikes);
                    a.notifyDataSetChanged();
                    lv.setAdapter(a);

                    Toast.makeText(bikelistActivity.this, "Carga correcta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(bikelistActivity.this, "Error en la carga del garaje", Toast.LENGTH_LONG).show();
            }
        }
    }
}
