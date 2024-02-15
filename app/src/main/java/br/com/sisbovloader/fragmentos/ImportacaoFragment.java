package br.com.sisbovloader.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sisbovloader.R;
import br.com.sisbovloader.fragmentos.recursos.GestorPDF;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportacaoFragment extends ListFragment {
    private static final String SISBOVS_NAO_SELECIONADOS = "SISBOVS_NAO_SELECIONADOS";
    private static final String SISBOVS_SELECIONADOS = "SISBOVS_SELECIONADOS";
    private List<String> sisbovsNaoSelecionados;
    private List<String> sisbovsSelecionados;
    private List<String> enderecos = null;
    private TextView tvwNavegacao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sisbovsNaoSelecionados = getArguments().getStringArrayList(SISBOVS_NAO_SELECIONADOS);
            sisbovsSelecionados = getArguments().getStringArrayList(SISBOVS_SELECIONADOS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_importacao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvwNavegacao = getActivity().findViewById(R.id.tvwNavegacao);
        String raiz = Environment.getExternalStorageDirectory().toString();

        listarDiretorio(raiz);
    }

    private void listarDiretorio(String raiz) {
        tvwNavegacao.setText("Você está em: " + raiz);
        List<String> item = new ArrayList<String>();
        enderecos = new ArrayList<>();
        File diretorio = new File(raiz);
        File[] arquivos = diretorio.listFiles();

        if (!diretorio.equals(raiz)) {
            item.add(raiz);
            enderecos.add(raiz);
            item.add("../");
            enderecos.add(diretorio.getParent());
        }

        for (File arquivo : arquivos)
            if (!arquivo.isHidden() && arquivo.canRead()) {
                enderecos.add(arquivo.getPath());
                if (arquivo.isDirectory())
                    item.add(arquivo.getName() + "/");
                else if (Objects.equals(obterExtensao(arquivo.getName()), "pdf"))
                    item.add(arquivo.getName());
            }

        ArrayAdapter<String> listaArquivos = new ArrayAdapter<>(getActivity(), R.layout.linha, item);
        setListAdapter(listaArquivos);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        File arquivo = new File(enderecos.get(position));

        if (arquivo.isDirectory()) {
            if (arquivo.canRead())
                listarDiretorio(enderecos.get(position));
            else
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setTitle("[" + arquivo.getName() + "] não pode ser lido.")
                        .setPositiveButton("OK", null).show();
        } else {
            sisbovsSelecionados.clear();
            sisbovsNaoSelecionados.clear();
            sisbovsNaoSelecionados.addAll(new GestorPDF().carregar(arquivo.getAbsolutePath()));
            BottomNavigationView navegacaoView = getActivity().findViewById(R.id.bottom_navigation);
            navegacaoView.setSelectedItemId(R.id.lista_menu);
        }
    }

    private String obterExtensao(String nomeArquivo) {
        int sufixoNomerquivo = nomeArquivo.lastIndexOf(".");
        return sufixoNomerquivo > 0 ? nomeArquivo.substring(sufixoNomerquivo + 1).toLowerCase() : null;
    }
}