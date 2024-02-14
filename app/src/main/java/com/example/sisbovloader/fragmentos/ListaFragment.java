package com.example.sisbovloader.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sisbovloader.R;
import com.example.sisbovloader.fragmentos.recursos.SisbovListaAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaFragment extends Fragment {
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
        sisbovsListView = new ArrayList<>(sisbovsNaoSelecionados);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtTotal = getView().findViewById(R.id.txtTotal);
        AtualizarLista(txtTotal);

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
                for (String sisbov : sisbovsNaoSelecionados)
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
        SisbovListaAdapter sisbovListaAdapter = new SisbovListaAdapter(getContext(), sisbovsNaoSelecionados, sisbovsSelecionados, sisbovsListView, txtTotal);

        if (!sisbovsListView.isEmpty()) {
            lvwSisbovs.setVisibility(View.VISIBLE);
            lvwSisbovs.setAdapter(sisbovListaAdapter);
        }
        return sisbovListaAdapter;
    }
}