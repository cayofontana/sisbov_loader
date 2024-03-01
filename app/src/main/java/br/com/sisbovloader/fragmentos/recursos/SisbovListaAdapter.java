package br.com.sisbovloader.fragmentos.recursos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import br.com.sisbovloader.PrincipalActivity;
import br.com.sisbovloader.R;
import br.com.sisbovloader.dados.SisbovDataAccess;

import java.util.List;

public class SisbovListaAdapter extends ArrayAdapter<String> {
    private Context contexto;
    private List<String> sisbovs;
    private TextView txtTotal;
    private SisbovDataAccess sisbovDataAccess;

    public SisbovListaAdapter(Context contexto, List<String> sisbovs, SisbovDataAccess sisbovDataAccess, TextView txtTotal) {
        super(contexto, R.layout.item_lista, sisbovs);
        this.contexto = contexto;
        this.sisbovs = sisbovs;
        this.sisbovDataAccess = sisbovDataAccess;
        this.txtTotal = txtTotal;
    }

    @Override
    public int getCount() {
        return sisbovs != null ? sisbovs.size() : 0;
    }

    @Override
    public String getItem(int i) {
        return (sisbovs != null) ? sisbovs.get(i) : null;
    }

    @Override
    public View getView(final int indice, View visaoConversao, ViewGroup elementoPai) {
        LayoutInflater preenchedorLeiaute = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View linhavisao = preenchedorLeiaute.inflate(R.layout.item_lista, elementoPai, false);
        TextView titulo = linhavisao.findViewById(R.id.titulo);
        AppCompatButton btnAcao = linhavisao.findViewById(R.id.btnAcao);

        titulo.setText(sisbovs.get(indice));
        PrincipalActivity principalActivity = (PrincipalActivity) contexto;
        if (principalActivity.getIdFragmento() == R.id.lista_menu)
            btnAcao.setText(R.string.incluir);
        else
            btnAcao.setText(R.string.remover);
        btnAcao.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            String sisbov = sisbovs.get(indice);
            sisbovDataAccess.atualizar(sisbov, principalActivity.getIdFragmento() == R.id.lista_menu ? 1 : 0);
            sisbovs.remove(sisbov);
            txtTotal.setText(String.valueOf(sisbovs.size()));
            notifyDataSetChanged();
        });

        return (linhavisao);
    }
}