package com.example.evaluacion_practica_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.evaluacion_practica_2.fragmentos.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);

        if (!session.estaLogueado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) cargarFragmento(new InicioFragment());
            else if (id == R.id.nav_productos) cargarFragmento(new ProductosFragment());
            else if (id == R.id.nav_clientes) cargarFragmento(new ClientesFragment());
            else if (id == R.id.nav_pedidos) cargarFragmento(new PedidosFragment());
            else if (id == R.id.nav_config) cargarFragmento(new ConfiguracionFragment());
            return true;
        });

        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment());
            bottomNav.setSelectedItemId(R.id.nav_inicio);
        }
    }

    private void cargarFragmento(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawer_inicio) cargarFragmento(new InicioFragment());
        else if (id == R.id.drawer_productos) cargarFragmento(new ProductosFragment());
        else if (id == R.id.drawer_clientes) cargarFragmento(new ClientesFragment());
        else if (id == R.id.drawer_pedidos) cargarFragmento(new PedidosFragment());
        else if (id == R.id.drawer_config) cargarFragmento(new ConfiguracionFragment());
        else if (id == R.id.drawer_salir) {
            session.cerrarSesion();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
