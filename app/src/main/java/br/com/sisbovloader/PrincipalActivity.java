package br.com.sisbovloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import br.com.sisbovloader.dados.SisbovDataAccess;
import br.com.sisbovloader.fragmentos.ImportacaoFragment;
import br.com.sisbovloader.fragmentos.ListaFragment;
import br.com.sisbovloader.fragmentos.SelecaoFragment;
import br.com.sisbovloader.fragmentos.SobreFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private Fragment fragmentoCorrente;
    private int idFragmentoCorrente;
    private String codigoDeBarras;

    public int getIdFragmento() {
        return idFragmentoCorrente;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        fragmentoCorrente = null;
        idFragmentoCorrente = -1;
        codigoDeBarras = "";

        BottomNavigationView navegacaoView = findViewById(R.id.bottom_navigation);
        navegacaoView.setSelectedItemId(idFragmentoCorrente);
        definirMenuInicial(navegacaoView);
        navegacaoView.setOnItemSelectedListener (item -> {
            if (idFragmentoCorrente == item.getItemId())
                return false;
            fragmentoCorrente = item.getItemId() == R.id.lista_menu ? new ListaFragment() : item.getItemId() == R.id.selecao_menu ? new SelecaoFragment() : item.getItemId() == R.id.importacao_menu ? new ImportacaoFragment() : new SobreFragment();
            idFragmentoCorrente = item.getItemId();
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragmentoCorrente).commit();
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SisbovDataAccess sisbovDataAccess = SisbovDataAccess.obterInstancia(this);
        sisbovDataAccess.fechar();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent evento) {
        if (evento.getKeyCode() == KeyEvent.KEYCODE_DEL || evento.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return super.dispatchKeyEvent(evento);

        if (evento.getAction() == KeyEvent.ACTION_DOWN && evento.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            char teclaPressionada = (char) evento.getUnicodeChar();
            codigoDeBarras += teclaPressionada;
        }

        if (evento.getAction() == KeyEvent.ACTION_UP  && evento.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            processarCodigoDeBarras();
            codigoDeBarras = "";
        }
        return false;
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultante, Intent intencao) {
        super.onActivityResult(codigoRequisicao, codigoResultante, intencao);
        if (idFragmentoCorrente == R.id.importacao_menu)
            ((ImportacaoFragment) fragmentoCorrente).processarArquivo(codigoRequisicao, codigoResultante, intencao);
    }

    private void definirMenuInicial(BottomNavigationView navegacaoView) {
        SisbovDataAccess sisbovDataAccess = SisbovDataAccess.obterInstancia(this);
        if (!sisbovDataAccess.listar(1).isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new SelecaoFragment()).commit();
            navegacaoView.setSelectedItemId(R.id.selecao_menu);
        }
        else if (!sisbovDataAccess.listar(0).isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ListaFragment()).commit();
            navegacaoView.setSelectedItemId(R.id.lista_menu);
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ImportacaoFragment()).commit();
            navegacaoView.setSelectedItemId(R.id.importacao_menu);
        }
    }

    private void processarCodigoDeBarras() {
        SisbovDataAccess sisbovDataAccess = SisbovDataAccess.obterInstancia(this);
        if (codigoDeBarras.length() > 15) {
            codigoDeBarras = codigoDeBarras.substring(codigoDeBarras.length() - 15);
            int situacaoDoSisbov = sisbovDataAccess.obterEstado(codigoDeBarras);
            if (situacaoDoSisbov == -1)
                Toast.makeText(this, "O SISBOV " + codigoDeBarras + " não foi encontrado.", Toast.LENGTH_SHORT).show();
            else if (situacaoDoSisbov == 1)
                Toast.makeText(this, "O SISBOV " + codigoDeBarras + " já está na lista de seleção.", Toast.LENGTH_LONG).show();
            else {
                sisbovDataAccess.atualizar(codigoDeBarras, 1);
                BottomNavigationView navegacaoView = findViewById(R.id.bottom_navigation);
                if (idFragmentoCorrente == R.id.selecao_menu)
                    navegacaoView.setSelectedItemId(R.id.lista_menu);
                navegacaoView.setSelectedItemId(R.id.selecao_menu);
            }
        }
    }
}