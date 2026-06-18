package com.example.evaluacion_practica_2;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MiniSuperPrefs";
    private static final String KEY_USUARIO = "usuario_logueado";
    private static final String KEY_NOMBRE = "nombre_usuario";
    private static final String KEY_ROL = "rol_usuario";
    private static final String KEY_TEMA = "tema_oscuro";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void guardarSesion(String usuario, String nombre, String rol) {
        prefs.edit().putString(KEY_USUARIO, usuario).putString(KEY_NOMBRE, nombre)
                .putString(KEY_ROL, rol).apply();
    }

    public void cerrarSesion() {
        prefs.edit().remove(KEY_USUARIO).remove(KEY_NOMBRE).remove(KEY_ROL).apply();
    }

    public boolean estaLogueado() {
        return prefs.getString(KEY_USUARIO, null) != null;
    }

    public String getUsuario() { return prefs.getString(KEY_USUARIO, ""); }
    public String getNombre() { return prefs.getString(KEY_NOMBRE, ""); }
    public String getRol() { return prefs.getString(KEY_ROL, ""); }

    public boolean isTemaOscuro() { return prefs.getBoolean(KEY_TEMA, false); }
    public void setTemaOscuro(boolean oscuro) { prefs.edit().putBoolean(KEY_TEMA, oscuro).apply(); }
}
