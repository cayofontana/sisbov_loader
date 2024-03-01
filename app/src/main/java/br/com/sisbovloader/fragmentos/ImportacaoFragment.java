package br.com.sisbovloader.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.sisbovloader.R;
import br.com.sisbovloader.dados.SisbovDataAccess;
import br.com.sisbovloader.fragmentos.recursos.GestorPDF;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ImportacaoFragment extends Fragment {
    private static final int CODIGO_REQUISICAO_PDF = 1;
    private boolean exibirDialogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exibirDialogo = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_importacao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatButton botaoPDF = getView().findViewById(R.id.botao_pdf);
        botaoPDF.setOnClickListener(v -> {
            localizarArquivoPDF();
        });

        AppCompatButton botaoInformacoes = getView().findViewById(R.id.botao_informacoes);
        botaoInformacoes.setOnClickListener(v -> {
            AlertDialog.Builder construtorDialogo = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            construtorDialogo.setView(inflater.inflate(R.layout.dialogo_informacoes_importacao, null));
            construtorDialogo.setPositiveButton("Fechar",null);
            AlertDialog dialogoInformacoes = construtorDialogo.create();
            dialogoInformacoes.show();
        });
    }

    private void localizarArquivoPDF() {
        Intent intencao = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intencao.setType("application/pdf");
        intencao.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intencao, CODIGO_REQUISICAO_PDF);
    }

    public void processarArquivo(int codigoRequisicao, int codigoResultante, Intent intencao) {
        if (codigoRequisicao == CODIGO_REQUISICAO_PDF && codigoResultante == Activity.RESULT_OK) {
            if (intencao != null) {
                Uri uri = intencao.getData();
                if (uri != null) {
                    try {
                        InputStream arquivo = getActivity().getContentResolver().openInputStream(uri);
                        List<String> sisbovExtrato = new GestorPDF().carregar(arquivo);

                        SisbovDataAccess sisbovDataAccess = SisbovDataAccess.obterInstancia(getContext());
                        sisbovDataAccess.removerTodos();
                        for (String sisbov : sisbovExtrato)
                            sisbovDataAccess.inserir(sisbov);
                        BottomNavigationView navegacaoView = getActivity().findViewById(R.id.bottom_navigation);
                        navegacaoView.setSelectedItemId(R.id.lista_menu);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}