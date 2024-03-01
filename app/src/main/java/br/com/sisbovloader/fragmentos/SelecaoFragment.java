package br.com.sisbovloader.fragmentos;

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

import br.com.sisbovloader.R;
import br.com.sisbovloader.dados.SisbovDataAccess;
import br.com.sisbovloader.fragmentos.recursos.GestorPDF;
import br.com.sisbovloader.fragmentos.recursos.SisbovListaAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelecaoFragment extends Fragment {
    private static final String SISBOVS_SELECIONADOS = "SISBOVS_SELECIONADOS";
    private List<String> sisbovs;
    private List<String> sisbovsListView;
    private SisbovListaAdapter sisbovListaAdapter;
    private SisbovDataAccess sisbovDataAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sisbovDataAccess = SisbovDataAccess.obterInstancia(getContext());
        sisbovs = sisbovDataAccess.listar(1);
        sisbovsListView = new ArrayList<>(sisbovs);
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

        sisbovListaAdapter = exibirSisbovs(txtTotal);
        AppCompatButton botaoLimparLista = getView().findViewById(R.id.btnLimparLista);
        botaoLimparLista.setOnClickListener(v -> {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());
            dialogo.setMessage(R.string.mensagem_dialogo_lista).setTitle(R.string.titulo_dialogo_lista);
            dialogo.setCancelable(false);
            dialogo.setPositiveButton("Sim", (dialog, which) -> {
                for (String sisbov : sisbovs)
                    sisbovDataAccess.atualizar(sisbov, 0);
                sisbovs.clear();
                sisbovsListView.clear();
                ListView lvwSisbovs = getView().findViewById(R.id.lvwSisbovs);
                lvwSisbovs.setAdapter(null);
                atualizarLista(sisbovListaAdapter, txtTotal);
            });
            dialogo.setNegativeButton("Não", null);
            AlertDialog dialogoAlerta = dialogo.create();
            dialogoAlerta.show();
        });

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
                for (String sisbov : sisbovs)
                    if (sisbov.contains(editavel.toString()))
                        sisbovsListView.add(sisbov);
                atualizarLista(sisbovListaAdapter, txtTotal);
            }
        });
        atualizarLista(sisbovListaAdapter, txtTotal);
    }

    private void atualizarLista(SisbovListaAdapter sisbovListaAdapter, @NonNull TextView txtTotal) {
        sisbovListaAdapter.notifyDataSetChanged();
        txtTotal.setText(String.valueOf(sisbovsListView.size()));
        sisbovs = sisbovDataAccess.listar(1);
    }

    @NonNull
    private SisbovListaAdapter exibirSisbovs(TextView txtTotal) {
        ListView lvwSisbovs = getView().findViewById(R.id.lvwSisbovs);
        SisbovListaAdapter sisbovListaAdapter = new SisbovListaAdapter(getContext(), sisbovsListView, sisbovDataAccess, txtTotal);
        lvwSisbovs.setAdapter(sisbovListaAdapter);
        return sisbovListaAdapter;
    }
}