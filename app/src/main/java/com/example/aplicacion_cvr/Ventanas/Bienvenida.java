package com.example.aplicacion_cvr.Ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aplicacion_cvr.Conexiones.Utils;
import com.example.aplicacion_cvr.R;

public class Bienvenida extends AppCompatActivity {
    TextView txtResultado_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bienvenida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtResultado_info = findViewById(R.id.lbEntrada);
        txtResultado_info.setText(String.format("Bienvenido, %s", Utils.getNombreTrabajador()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Bienvenida.this, Menu.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}