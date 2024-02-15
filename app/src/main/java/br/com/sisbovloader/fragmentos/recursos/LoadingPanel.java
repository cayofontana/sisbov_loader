package br.com.sisbovloader.fragmentos.recursos;

import android.app.AlertDialog;
import android.content.Context;

public class LoadingPanel {
    private static AlertDialog dialogo = null;

    public static void exibir(Context context, String message) {
        if (dialogo == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Sisbov Loader");
            alertDialogBuilder.setMessage("Carregando...").setCancelable(false);
            dialogo = alertDialogBuilder.create();
            dialogo.show();
        }
    }

    public static void finalizar() {
        if (dialogo != null) {
            dialogo.dismiss();
            dialogo = null;
        }
    }
}