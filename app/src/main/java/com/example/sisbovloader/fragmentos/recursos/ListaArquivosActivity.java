package com.example.sisbovloader.fragmentos.recursos;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListaArquivosActivity extends ListActivity {

    File root;
    File pdf;

    private List<String> fileList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        // ListDir(root);

        pdf = new File(root, "PDF");
        ListDir(pdf);
    }

    void ListDir(File f) {
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files) {
            fileList.add(file.getPath());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);

        setListAdapter(directoryList);

    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        //selection.setText(fileList.indexOf(simple_list_item_1));
        OpenPdf(fileList.get(position).toString());
    }

    public void OpenPdf(String path) {
        File file = new File(path);
        if (file.exists()) {
            Uri p = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(p, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }
        }
    }
}
