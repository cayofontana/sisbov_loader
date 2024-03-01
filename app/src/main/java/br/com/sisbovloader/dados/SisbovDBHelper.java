package br.com.sisbovloader.dados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SisbovDBHelper extends SQLiteOpenHelper {
    public static final int VERSAO_BANCO_DE_DADOS = 3;
    public static final String NOME_BANCO_DE_DADOS = "sisbov.db";

    public SisbovDBHelper(Context context) {
        super(context, NOME_BANCO_DE_DADOS, null, VERSAO_BANCO_DE_DADOS);
    }

    public void onCreate(SQLiteDatabase bancoDeDados) {
        bancoDeDados.execSQL(SisbovContract.Sisbov.CRIACAO_TABELA);
    }

    public void onUpgrade(SQLiteDatabase bancoDeDados, int versaoAnterior, int novaVersao) {
        bancoDeDados.execSQL(SisbovContract.Sisbov.EXCLUSAO_TABELA);
        onCreate(bancoDeDados);
    }

    public void onDowngrade(SQLiteDatabase bancoDeDados, int versaoAnterior, int novaVersao) {
        onUpgrade(bancoDeDados, versaoAnterior, novaVersao);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}