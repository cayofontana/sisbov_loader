package br.com.sisbovloader.dados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SisbovDataAccess {
    private static SisbovDataAccess instancia;
    private SisbovDBHelper sisbovDBHelper;
    private SQLiteDatabase bancoDedados;
    private Context contexto;

    private SisbovDataAccess(Context contexto) {
        this.contexto = contexto;
        abrir();
    }

    public static SisbovDataAccess obterInstancia(Context contexto) {
        if (instancia == null)
            instancia = new SisbovDataAccess(contexto);
        return instancia;
    }

    public SisbovDataAccess abrir() throws SQLException {
        if (sisbovDBHelper == null || !sisbovDBHelper.getWritableDatabase().isOpen()) {
            sisbovDBHelper = new SisbovDBHelper(contexto);
            bancoDedados = sisbovDBHelper.getWritableDatabase();
        }
        return this;
    }

    public void fechar() {
        if (sisbovDBHelper != null && sisbovDBHelper.getWritableDatabase().isOpen())
            sisbovDBHelper.close();
    }

    public long inserir(String sisbov) {
        ContentValues valores = new ContentValues();
        valores.put(SisbovContract.Sisbov.NOME_COLUNA_SISBOV, sisbov);
        return bancoDedados.insert(SisbovContract.Sisbov.NOME_TABELA, null, valores);
    }

    public List<String> listar(int flag) {
        String[] projecao = { SisbovContract.Sisbov.NOME_COLUNA_SISBOV, SisbovContract.Sisbov.NOME_COLUNA_FLAG };
        String filtro = SisbovContract.Sisbov.NOME_COLUNA_FLAG + " = ?";
        String[] argumentosFiltro = { String.valueOf(flag) };
        String ordenacao = SisbovContract.Sisbov.NOME_COLUNA_SISBOV + " DESC";
        Cursor cursor = bancoDedados.query(SisbovContract.Sisbov.NOME_TABELA, projecao, filtro, argumentosFiltro, null, null, ordenacao);

        List<String> sisbovs = new ArrayList<>();
        while (cursor.moveToNext())
            sisbovs.add(cursor.getString(cursor.getColumnIndexOrThrow(SisbovContract.Sisbov.NOME_COLUNA_SISBOV)));
        cursor.close();
        return sisbovs;
    }

    public int obterEstado(String sisbov) {
        String[] projecao = { SisbovContract.Sisbov.NOME_COLUNA_FLAG };
        String filtro = SisbovContract.Sisbov.NOME_COLUNA_SISBOV + " = ?";
        String[] argumentosFiltro = { sisbov };
        Cursor cursor = bancoDedados.query(SisbovContract.Sisbov.NOME_TABELA, projecao, filtro, argumentosFiltro, null, null, null);

        int estado = -1;
        if (cursor != null && cursor.getCount() == 1 && cursor.moveToNext())
            estado = cursor.getInt(cursor.getColumnIndexOrThrow(SisbovContract.Sisbov.NOME_COLUNA_FLAG));
        cursor.close();
        return estado;
    }

    public int atualizar(String sisbov, int flag) {
        ContentValues valores = new ContentValues();
        valores.put(SisbovContract.Sisbov.NOME_COLUNA_FLAG, flag);
        return bancoDedados.update(SisbovContract.Sisbov.NOME_TABELA, valores, SisbovContract.Sisbov.NOME_COLUNA_SISBOV + " = '" + sisbov + "'", null);
    }

    public void remover(String sisbov) {
        bancoDedados.delete(SisbovContract.Sisbov.NOME_TABELA, SisbovContract.Sisbov.NOME_COLUNA_SISBOV + " = '" + sisbov + "'", null);
    }

    public void removerTodos() {
        bancoDedados.delete(SisbovContract.Sisbov.NOME_TABELA, null, null);
    }
}