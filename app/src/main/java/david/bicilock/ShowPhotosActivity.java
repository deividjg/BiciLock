package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
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

public class ShowPhotosActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerView;
    //adapter object
    private RecyclerView.Adapter adapter;
    //list to hold all the uploaded images
    private List<Photo> photos;

    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private Photo photo;
    private ArrayList<Photo> arrayPhotos;
    private String serialNumber;
    private String id;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        returnJSON = new ReturnJSON();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                photo = arrayPhotos.get(pos);
                Intent intent = new Intent (getApplicationContext(), ImageDetailActivity.class);
                intent.putExtra("url", photo.getUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                pos = position;
                showConfirmDialog();
            }
        }));

        getBikePhotos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addPhotoShow) {
            Intent intent = new Intent (this, UploadPhotosActivity.class);
            intent.putExtra("serialNumber", serialNumber);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
        AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(ShowPhotosActivity.this);
        alertDialogBu.setTitle(R.string.remove_photo);
        alertDialogBu.setMessage(R.string.are_you_sure);
        alertDialogBu.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBu.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialogBu.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                photo = arrayPhotos.get(pos);
                id = photo.getId();
                new DeletePhotoTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    ///////Task for download bike photos
    class DownloadPhotosTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ShowPhotosActivity.this);
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
                Toast.makeText(ShowPhotosActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////Task for delete a photo
    class DeletePhotoTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ShowPhotosActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "DELETE FROM photos WHERE id = '" + id + "'");

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

                if (add != 0) {
                    Toast.makeText(ShowPhotosActivity.this, R.string.successfully_removed, Toast.LENGTH_SHORT).show();
                    arrayPhotos.remove(pos);
                    adapter.notifyDataSetChanged();
                    delete();
                } else {
                    Toast.makeText(ShowPhotosActivity.this, R.string.error_removing, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ShowPhotosActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void delete() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference toDeleteFile = storageReference.child("images/" + serialNumber + "/" + id);

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