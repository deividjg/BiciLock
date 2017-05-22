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

    private String url_consulta, serialNumber;
    private JSONArray jSONArray;
    private ReturnJSON returnJSON;
    private Bike bike;
    private ArrayList<Bike> arrayBikes;
    ArrayList<HashMap<String, String>> bikeList;

    private EditText etSerialNumberCheckBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bike);

        etSerialNumberCheckBike = (EditText)findViewById(R.id.etSerialNumberCheckBike);

        url_consulta = "http://iesayala.ddns.net/deividjg/php.php";
        returnJSON = new ReturnJSON();
    }

    public void checkSerialNumber(View view){
        serialNumber = etSerialNumberCheckBike.getText().toString();
        new CheckBike().execute();
    }

    ///////Task para comprobar número de serie
    class CheckBike extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(CheckBikeActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM bikes WHERE SerialNumber='" + serialNumber + "'");

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
                        bike.setStolen(jsonObject.getInt("Stolen"));
                        bike.setDetails("Details");
                        arrayBikes.add(bike);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (arrayBikes.size() > 0) {
                    if (bike.stolen == 1) {
                        showConfirmDialog();
                    } else {
                        Toast.makeText(CheckBikeActivity.this, "Esta bicicleta no está denunciada", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CheckBikeActivity.this, "La bicicleta no se encuentra en la base de datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CheckBikeActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void showConfirmDialog(){
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(CheckBikeActivity.this);
        alertDialogBu.setTitle("Bicicleta Robada!");
        alertDialogBu.setMessage("¿Quieres avisar al propietario?");
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Boton Rechazar pulsado",
                        Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBu.setPositiveButton( "Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Boton Aceptar pulsado",
                        Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    protected void notifyScreen(){
        Intent intent = new Intent (this, BikelistActivity.class);
        //intent.putExtra("email", email);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
