package com.example.sisbovloader.fragmentos;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sisbovloader.R;
import com.example.sisbovloader.fragmentos.recursos.GestorPDF;
import com.example.sisbovloader.fragmentos.recursos.SisbovListaAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelecaoFragment extends Fragment {
    private static final String SISBOVS_NAO_SELECIONADOS = "SISBOVS_NAO_SELECIONADOS";
    private static final String SISBOVS_SELECIONADOS = "SISBOVS_SELECIONADOS";
    private List<String> sisbovsNaoSelecionados;
    private List<String> sisbovsSelecionados;
    private List<String> sisbovsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sisbovsNaoSelecionados = getArguments().getStringArrayList(SISBOVS_NAO_SELECIONADOS);
            sisbovsSelecionados = getArguments().getStringArrayList(SISBOVS_SELECIONADOS);
        }
        sisbovsListView = new ArrayList<>(sisbovsSelecionados);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selecao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        TextView txtTotal = getView().findViewById(R.id.txtTotal);
        AppCompatButton botaoCopiarLista = getView().findViewById(R.id.btnCopiarLista);
        AtualizarLista(txtTotal);

        botaoCopiarLista.setOnClickListener(v -> {
            if (sisbovsListView.isEmpty())
                Toast.makeText(getContext(), "A lista está vazia.", Toast.LENGTH_SHORT).show();
            else {
                StringBuilder listagem = new StringBuilder();
                for (int i = 0; i < sisbovsListView.size(); i++)
                    listagem.append(i == sisbovsListView.size() - 1 ? sisbovsListView.get(i) : sisbovsListView.get(i) + "\n");
                clipboard.setPrimaryClip(ClipData.newPlainText(SISBOVS_SELECIONADOS, listagem.toString()));
            }
        });

        AppCompatButton botaoGerarPDF = getView().findViewById(R.id.btnGerarpdf);
        botaoGerarPDF.setOnClickListener(v -> {
            if (sisbovsListView.isEmpty())
                Toast.makeText(getContext(), "A lista está vazia.", Toast.LENGTH_SHORT).show();
            else {
                GestorPDF gestorPDF = new GestorPDF();
                gestorPDF.criar(sisbovsListView);
                if (gestorPDF.gerouArquivo())
                    Toast.makeText(getContext(), "Criado /Documents" + gestorPDF.getNomeArquivo(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "O arquivo não pôde ser criado. Informe a equipe de suporte.", Toast.LENGTH_SHORT).show();
            }
        });

        AppCompatButton botaoLimparLista = getView().findViewById(R.id.btnLimparLista);
        botaoLimparLista.setOnClickListener(v -> {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());
            dialogo.setMessage(R.string.mensagem_dialogo_lista).setTitle(R.string.titulo_dialogo_lista);
            dialogo.setCancelable(false);
            dialogo.setPositiveButton("Sim", (dialog, which) -> {
                sisbovsNaoSelecionados.addAll(sisbovsSelecionados);
                sisbovsSelecionados.clear();
                sisbovsListView.clear();
                ListView lvwSisbovs = getView().findViewById(R.id.lvwSisbovs);
                lvwSisbovs.setAdapter(null);
                AtualizarLista(txtTotal);
            });
            dialogo.setNegativeButton("Não", null);
            AlertDialog dialogoAlerta = dialogo.create();
            dialogoAlerta.show();
        });

        final SisbovListaAdapter sisbovListaAdapter = exibirSisbovs(txtTotal);

        AppCompatEditText pesquisaEditText = getView().findViewById(R.id.pesquisa);
        pesquisaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editavel) {
                sisbovsListView.clear();
                for (String sisbov : sisbovsSelecionados)
                    if (sisbov.contains(editavel.toString()))
                        sisbovsListView.add(sisbov);
                AtualizarLista(txtTotal);
                sisbovListaAdapter.notifyDataSetChanged();
            }
        });
    }

    private void AtualizarLista(@NonNull TextView txtTotal) {
        txtTotal.setText(String.valueOf(sisbovsListView.size()));
    }

    @NonNull
    private SisbovListaAdapter exibirSisbovs(TextView txtTotal) {
        ListView lvwSisbovs = getView().findViewById(R.id.lvwSisbovs);
        SisbovListaAdapter sisbovListaAdapter = new SisbovListaAdapter(getContext(), sisbovsSelecionados, sisbovsNaoSelecionados, sisbovsListView, txtTotal);

        if (!sisbovsListView.isEmpty()) {
            lvwSisbovs.setVisibility(View.VISIBLE);
            lvwSisbovs.setAdapter(sisbovListaAdapter);
        }
        return sisbovListaAdapter;
    }
}