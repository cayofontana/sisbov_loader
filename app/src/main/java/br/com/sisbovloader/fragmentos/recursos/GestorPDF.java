package br.com.sisbovloader.fragmentos.recursos;

import android.os.Environment;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GestorPDF {
    private boolean arquivoGerado;
    private String nomeArquivo;

    public void criar(List<String> sisbovs) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/Documents");
            f = new File(f.getAbsolutePath());
            if (!f.exists()) {
                f.mkdir();
            }

            final DateFormat formatoData = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Calendar calendario = Calendar.getInstance();
            nomeArquivo = "/boiid_lista" + formatoData.format(calendario.getTime()) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(f.getAbsolutePath() + nomeArquivo));
            document.open();
            document.add(new Chunk(""));
            document.add(new Paragraph("Boi ID - Identificação Bovina por Visão Computacional"));
            document.add(new Paragraph("Total de bovinos (SISBOVs): " + sisbovs.size()));
            document.add(new Paragraph(" "));
            for (String sisbov : sisbovs)
                document.add(new Paragraph(sisbov));
            arquivoGerado = true;
        } catch (Exception excecao) {
            excecao.printStackTrace();
            arquivoGerado = false;
        }
        document.close();
    }

    public List<String> carregar(String endereco) {
        List<String> sisbovs = new ArrayList<>();
        try {
            PdfReader leitor = new PdfReader(endereco);
            int totalPaginas = leitor.getNumberOfPages();
            String conteudoPagina = "";
            for (int paginaAtual = 1; paginaAtual <= totalPaginas; paginaAtual++) {
                conteudoPagina = PdfTextExtractor.getTextFromPage(leitor, paginaAtual);
                sisbovs.addAll(obterSisbovs(conteudoPagina));
            }
        }
        catch (Exception excecao) {
            excecao.printStackTrace();
        }
        return sisbovs;
    }

    public boolean gerouArquivo() {
        return arquivoGerado;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    private List<String> obterSisbovs(String textoPagina) {
        List<String> sisbovs = new ArrayList<>();
        String[] linhas = textoPagina.split("\n");
        for (String linha : linhas) {
            String[] palavras = linha.split(" ");
            for (String palavra : palavras)
                if (ehSisbov(palavra))
                    sisbovs.add(palavra);
        }
        return sisbovs;
    }

    private boolean ehSisbov(String palavra) {
        if (palavra.length() == 15) {
            for (int i = 0; i < palavra.length(); i++)
                if (!Character.isDigit(palavra.charAt(i)))
                    return false;
            return true;
        }
        return false;
    }
}