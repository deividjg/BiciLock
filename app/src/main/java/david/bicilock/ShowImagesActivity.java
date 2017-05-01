package david.bicilock;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class ShowImagesActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //list to hold all the uploaded images
    private List<Upload> uploads;


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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                Toast.makeText(getApplicationContext(), "Long press on position :"+position, Toast.LENGTH_LONG).show();
            }

        }));

        uploads = new ArrayList<>();

        //displaying progress dialog while fetching images

        Upload upload = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/bancos-de-imagenes.jpg");
        Upload upload2 = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/bancos-de-imagenes-gratis.jpg");
        Upload upload3 = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/banco-de-imagenes-gratis.jpg");
        Upload upload4 = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/bancos-imagenes-gratuitas.jpg");
        Upload upload5 = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/imagenes-gratis.jpg");
        Upload upload6 = new Upload("nombre", "http://esyourself.wpengine.netdna-cdn.com/wp-content/uploads/2013/06/banco-de-imagenes-gratuito.jpg");
        uploads.add(upload);
        uploads.add(upload2);
        uploads.add(upload3);
        uploads.add(upload4);
        uploads.add(upload5);
        uploads.add(upload6);

        adapter = new MyAdapter(getApplicationContext(), uploads);

        //adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
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
}
