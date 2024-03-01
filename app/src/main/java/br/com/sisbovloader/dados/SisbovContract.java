package br.com.sisbovloader.dados;

import android.provider.BaseColumns;

public class SisbovContract {
    private SisbovContract() {}

    public static class Sisbov implements BaseColumns {
        public static final String NOME_TABELA = "SISBOV";
//        public static final String NOME_COLUNA_ID = "SISB_ID_SISBOV";
        public static final String NOME_COLUNA_SISBOV = "SISB_CD_SISBOV";
        public static final String NOME_COLUNA_FLAG = "SISB_FL_SELECAO";
//        public static final String NOME_COLUNA_TIPO_LISTA = "SISB_CD_TIPO_LISTA";
        public static final String CRIACAO_TABELA =
                "CREATE TABLE " + NOME_TABELA + " (" +
//                        NOME_COLUNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NOME_COLUNA_SISBOV + " TEXT PRIMARY KEY, " +
                        NOME_COLUNA_FLAG + " INTEGER NOT NULL DEFAULT 0 " + // (1 = SISBOV selecionado; 0 = SISBOV não selecionado)
//                        NOME_COLUNA_TIPO_LISTA + " INTEGER NOT NULL DEFAULT 1 " + // (1 = Importação; 0 = Sem Importação)
                        ");";
        public static final String EXCLUSAO_TABELA = "DROP TABLE IF EXISTS " + NOME_TABELA;
    }
}