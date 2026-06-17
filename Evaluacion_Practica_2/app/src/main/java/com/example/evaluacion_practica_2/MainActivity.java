package com.example.evaluacion_practica_2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottonNavigation = findViewById(R.id.bottom_navigation);

        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null){
            CambiarFragmento(new InicioFragment());
        }

        bottonNavigation.setOnItemSelectedListener(
                item -> {
                    int id = item.getItemId();

                    if (id == R.id.nav_inicio) {
                        return CambiarFragmento(new InicioFragment());

                    } else if (id == R.id.nav_clientes) {
                        return CambiarFragmento(new ClientesFragment());
                    }else if(id == R.id.nav_servicios){
                        return CambiarFragmento(new ServiciosFragment());
                    } else if (id == R.id.nav_presupuesto) {
                        return  CambiarFragmento(new PresupuestoFragment());
                    } else if (id==R.id.nav_agenda) {
                        return CambiarFragmento(new AgendaFragment());
                    }
                    return false;
                }
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean CambiarFragmento(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}