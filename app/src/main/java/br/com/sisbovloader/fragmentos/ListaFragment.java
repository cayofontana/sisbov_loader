package br.com.sisbovloader.fragmentos;

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

import br.com.sisbovloader.R;
import br.com.sisbovloader.dados.SisbovDataAccess;
import br.com.sisbovloader.fragmentos.recursos.SisbovListaAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaFragment extends Fragment {
    private List<String> sisbovs;
    private List<String> sisbovsListView;
    private SisbovDataAccess sisbovDataAccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sisbovDataAccess = SisbovDataAccess.obterInstancia(getContext());
        sisbovs = sisbovDataAccess.listar(0);
        sisbovsListView = new ArrayList<>(sisbovs);
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
                for (String sisbov : sisbovs)
                    if (sisbov.contains(editavel.toString()))
                        sisbovsListView.add(sisbov);
                AtualizarLista(txtTotal);
                sisbovListaAdapter.notifyDataSetChanged();
            }
        });
    }

    private void AtualizarLista(@NonNull TextView txtTotal) {
        txtTotal.setText(String.valueOf(sisbovsListView.size()));
        sisbovs = sisbovDataAccess.listar(0);
    }

    @NonNull
    private SisbovListaAdapter exibirSisbovs(TextView txtTotal) {
        ListView lvwSisbovs = getView().findViewById(R.id.lvwSisbovs);
        SisbovListaAdapter sisbovListaAdapter = new SisbovListaAdapter(getContext(), sisbovsListView, sisbovDataAccess, txtTotal);
        lvwSisbovs.setAdapter(sisbovListaAdapter);
        return sisbovListaAdapter;
    }
}