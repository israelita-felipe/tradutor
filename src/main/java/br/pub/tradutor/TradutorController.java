/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pub.tradutor;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Cliente
 */
@Named(value = "tradutorController")
@SessionScoped
public class TradutorController implements Serializable {

    /**
     * Creates a new instance of TradutorController
     */
    public TradutorController() {
    }
    //URL que recebe as solicitações de tradução
    private static final String TRANSLATOR = "http://translate.google.com/translate_a/t?client=t&text=%s&hl=pt-BR&sl=%s&tl=%s&ie=UTF-8&oe=UTF-8&multires=1&prev=enter&ssel=5&tsel=5&sc=1";

    //Pattern utilizado para encontrar os resultados de tradução
    private static final Pattern RESULTS_PATTERN = Pattern.compile("\\[\"(.*?)\",\"(.*?)\",\"(.*?)\",\"(.*?)\"\\]");

    /**
     * Método utilizado para tradução utilizando o Google Translate
     *
     * @param text Texto a ser traduzido
     * @param from Idioma de origem
     * @param to Idioma de destino
     * @return Texto traduzido (no idioma destino)
     * @throws IOException
     */
    public static String translate(String text, String from, String to) throws IOException {
        //Faz encode de URL, para fazer escape dos valores que vão na URL
        String encodedText = URLEncoder.encode(text, "UTF-8");

        DefaultHttpClient httpclient = new DefaultHttpClient();

        //Método GET a ser executado
        HttpGet httpget = new HttpGet(String.format(TRANSLATOR, encodedText, from, to));

        //Faz a execução
        HttpResponse response = httpclient.execute(httpget);

        //Busca a resposta da requisição e armazena em String
        String returnContent = EntityUtils.toString(response.getEntity());

        //Desconsidera tudo depois do primeiro array
        returnContent = returnContent.split("\\],\\[")[0];

        //StringBuilder que sera carregado o retorno
        StringBuilder translatedText = new StringBuilder();

        //Verifica todas as traduções encontradas, e junta todos os trechos
        Matcher m = RESULTS_PATTERN.matcher(returnContent);
        while (m.find()) {
            translatedText.append(m.group(1).trim()).append(' ');
        }

        //Retorna
        return translatedText.toString().trim();

    }
}
