package br.com.sisbovloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Toast;

import br.com.sisbovloader.fragmentos.ImportacaoFragment;
import br.com.sisbovloader.fragmentos.ListaFragment;
import br.com.sisbovloader.fragmentos.SelecaoFragment;
import br.com.sisbovloader.fragmentos.SobreFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {
    static final String SISBOVS_NAO_SELECIONADOS = "SISBOVS_NAO_SELECIONADOS";
    static final String SISBOVS_SELECIONADOS = "SISBOVS_SELECIONADOS";
    private Fragment fragmentoCorrente;
    private int idFragmentoCorrente;
    private ArrayList<String> sisbovsNaoSelecionados;
    private ArrayList<String> sisbovsSelecionados;
    private String codigoDeBarras;

    public int getIdFragmento() {
        return idFragmentoCorrente;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:br.com.sisbovloader"));
            startActivity(intent);
        }

        fragmentoCorrente = null;
        idFragmentoCorrente = -1;
        sisbovsNaoSelecionados = new ArrayList<>();
        sisbovsSelecionados = new ArrayList<>();
        codigoDeBarras = "";

        BottomNavigationView navegacaoView = findViewById(R.id.bottom_navigation);
        navegacaoView.setSelectedItemId(idFragmentoCorrente);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ImportacaoFragment()).commit();
        navegacaoView.setOnItemSelectedListener (item -> {
            if (idFragmentoCorrente == item.getItemId())
                return false;
            if (idFragmentoCorrente < 0 || item.getItemId() == R.id.importacao_menu || item.getItemId() == R.id.lista_menu || item.getItemId() == R.id.selecao_menu) {
                Bundle pacote = new Bundle();
                pacote.putStringArrayList(SISBOVS_NAO_SELECIONADOS, sisbovsNaoSelecionados);
                pacote.putStringArrayList(SISBOVS_SELECIONADOS, sisbovsSelecionados);
                fragmentoCorrente = item.getItemId() == R.id.lista_menu ? new ListaFragment() : item.getItemId() == R.id.selecao_menu ? new SelecaoFragment() : new ImportacaoFragment();
                fragmentoCorrente.setArguments(pacote);
            }
            else
                fragmentoCorrente = new SobreFragment();
            idFragmentoCorrente = item.getItemId();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragmentoCorrente).commit();
            return true;
        });
        navegacaoView.setSelectedItemId(R.id.importacao_menu);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent evento) {
        if (evento.getKeyCode() == KeyEvent.KEYCODE_DEL)
            return super.dispatchKeyEvent(evento);

        if (evento.getAction() == KeyEvent.ACTION_DOWN && evento.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            char teclaPressionada = (char) evento.getUnicodeChar();
            codigoDeBarras += teclaPressionada;
        }

        if (evento.getAction() == KeyEvent.ACTION_UP  && evento.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (codigoDeBarras.length() > 15)
                codigoDeBarras = codigoDeBarras.substring(codigoDeBarras.length() - 15);
            if (!sisbovsNaoSelecionados.contains(codigoDeBarras) && !sisbovsSelecionados.contains(codigoDeBarras))
                Toast.makeText(getApplicationContext(), "O SISBOV " + codigoDeBarras + " não foi encontrado.", Toast.LENGTH_SHORT).show();
            else if (sisbovsSelecionados.contains(codigoDeBarras))
                Toast.makeText(getApplicationContext(), "O SISBOV " + codigoDeBarras + " já está na lista de seleção.", Toast.LENGTH_LONG).show();
            else {
                sisbovsSelecionados.add(codigoDeBarras);
                sisbovsNaoSelecionados.remove(codigoDeBarras);
                BottomNavigationView navegacaoView = findViewById(R.id.bottom_navigation);
                if (idFragmentoCorrente == R.id.selecao_menu)
                    navegacaoView.setSelectedItemId(R.id.lista_menu);
                navegacaoView.setSelectedItemId(R.id.selecao_menu);
            }
            codigoDeBarras = "";
        }
        return false;
    }
}