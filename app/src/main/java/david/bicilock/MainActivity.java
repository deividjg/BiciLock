package david.bicilock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sp;
    Button buttonLogout, buttonRegister, buttonModifyPersonalData, buttonManageGarage, btnLogin;
    TextView tvEmail;
    Menu nav_Menu;
    TextView nav_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nav_Menu = navigationView.getMenu();
        View hView =  navigationView.getHeaderView(0);
        nav_user = (TextView)hView.findViewById(R.id.tvNavHeader);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        tvEmail = (TextView) findViewById(R.id.tvEmailMain);
        buttonModifyPersonalData = (Button) findViewById(R.id.buttonModifyPersonalData);
        buttonManageGarage = (Button) findViewById(R.id.btnManageGarage);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        if (alreadyLogged()) {
            tvEmail.setText(sp.getString("email", "Invitado"));
            buttonRegister.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);

            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_register).setVisible(false);
            nav_user.setText(sp.getString("email", "Invitado"));

        } else {
            buttonLogout.setVisibility(View.INVISIBLE);
            buttonManageGarage.setVisibility(View.INVISIBLE);
            buttonModifyPersonalData.setVisibility(View.INVISIBLE);

            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_manage).setVisible(false);
            nav_user.setText(R.string.guest);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item_lv clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_check) {
            Intent intent = new Intent(this, CheckBikeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, BikelistActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("logged", false);
            editor.commit();
            recreate();
        } else if (id == R.id.nav_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void registerScreen(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginScreen(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void bikeListScreen(View view) {
        Intent intent = new Intent(this, BikelistActivity.class);
        startActivity(intent);
    }

    public void checkBikeScreen(View view) {
        Intent intent = new Intent(this, CheckBikeActivity.class);
        startActivity(intent);
    }

    public boolean alreadyLogged() {
        sp = getSharedPreferences("preferences", this.MODE_PRIVATE);
        return sp.getBoolean("logged", false);
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("logged", false);
        editor.commit();
        recreate();
    }
}
