package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.List;

public class ShowImagesActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //list to hold all the uploaded images
    private List<Upload> uploads;

    private String url_consulta, url_borrado, email, numSerie;
    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private Upload upload;
    private ArrayList<Upload> arrayUploads;
    ArrayList<HashMap<String, String>> uploadList;
    private String serialNumber;
    private String id;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrar();
            }
        });

        url_consulta = "http://iesayala.ddns.net/deividjg/php.php";
        url_borrado = "http://iesayala.ddns.net/deividjg/prueba.php";
        returnJSON = new ReturnJSON();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Toast.makeText(getApplicationContext(), "Single Click on position:" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                pos = position;
                showConfirmDialog();
            }
        }));

        getBikePhotos();
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    protected void getBikePhotos() {
        serialNumber = getIntent().getExtras().getString("serialNumber");
        new DownloadPhotosTask().execute();
    }

    protected void showConfirmDialog() {
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(ShowImagesActivity.this);
        alertDialogBu.setTitle("Eliminar bicicleta");
        alertDialogBu.setMessage("¿Estás seguro?");
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Boton Rechazar pulsado", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBu.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                upload = arrayUploads.get(pos);
                id = upload.getId();
                Toast.makeText(ShowImagesActivity.this, id, Toast.LENGTH_SHORT).show();
                new DeletePhotoTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    ///////Task para descargar las fotos de una bicicleta
    class DownloadPhotosTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ShowImagesActivity.this);
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
                arrayUploads = new ArrayList<Upload>();
                long position;
                for (int i = 0; i < json.length(); i++) {
                    position = i;
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        upload = new Upload();
                        upload.setPosition(position);
                        upload.setId(jsonObject.getString("id"));
                        upload.setSerialNumber(jsonObject.getString("SerialNumber"));
                        upload.setUrl(jsonObject.getString("url"));

                        arrayUploads.add(upload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new MyAdapter(getApplicationContext(), arrayUploads);
                    //adding adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(ShowImagesActivity.this, "Carga correcta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ShowImagesActivity.this, "Error en la carga del garaje", Toast.LENGTH_LONG).show();
            }
        }
    }

    ///////Task para eliminar una foto
    class DeletePhotoTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ShowImagesActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "DELETE FROM photos WHERE id = '" + id + "'");

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

                if (add != 0) {
                    Toast.makeText(ShowImagesActivity.this, "Registro borrado", Toast.LENGTH_LONG).show();
                    arrayUploads.remove(pos);
                    adapter.notifyDataSetChanged();
                    borrar();
                } else {
                    Toast.makeText(ShowImagesActivity.this, "Error al borrar", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ShowImagesActivity.this, "JSON Array nulo", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void borrar() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        System.out.println(storageReference.toString());

        StorageReference toDeleteFile = storageReference.child("images/" + serialNumber + "/" + id + ".jpg");

        System.out.println(toDeleteFile.toString());

        toDeleteFile.delete().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(ShowImagesActivity.this, "Foto borrada del servidor", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ShowImagesActivity.this, "Foto no borrada del servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}