package br.com.sisbovloader.fragmentos.recursos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

import br.com.sisbovloader.PrincipalActivity;
import br.com.sisbovloader.R;

public class SisbovSemListaAdapter extends ArrayAdapter<String> {
    private Context contexto;
    private List<String> sisbovs;
    private List<String> sisbovsListView;
    private TextView txtTotal;

    public SisbovSemListaAdapter(Context contexto, List<String> sisbovs, List<String> sisbovsListView, TextView txtTotal) {
        super(contexto, R.layout.item_lista, sisbovsListView);
        this.contexto = contexto;
        this.sisbovs = sisbovs;
        this.sisbovsListView = sisbovsListView;
        this.txtTotal = txtTotal;
    }

    @Override
    public int getCount() {
        return sisbovsListView != null ? sisbovsListView.size() : 0;
    }

    @Override
    public String getItem(int i) {
        return (sisbovsListView != null) ? sisbovsListView.get(i) : null;
    }

    @NonNull
    @Override
    public View getView(final int indice, View visaoConversao, ViewGroup elementoPai) {
        LayoutInflater preenchedorLeiaute = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View linhavisao = preenchedorLeiaute.inflate(R.layout.item_lista, elementoPai, false);
        TextView titulo = linhavisao.findViewById(R.id.titulo);
        AppCompatButton btnAcao = linhavisao.findViewById(R.id.btnAcao);

        titulo.setText(sisbovsListView.get(indice));
        btnAcao.setText(R.string.remover);
        btnAcao.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            sisbovsListView.remove(indice);
            sisbovs.remove(indice);
            txtTotal.setText(String.valueOf(sisbovsListView.size()));
            notifyDataSetChanged();
        });

        return (linhavisao);
    }
}