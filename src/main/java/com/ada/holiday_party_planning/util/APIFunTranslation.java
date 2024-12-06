package com.ada.holiday_party_planning.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utilitário para tradução de mensagens usando a API FunTranslations.
 * <p>
 * Essa classe permite traduzir um texto para uma categoria específica utilizando a API FunTranslations.
 * O método `tranlateFun` faz uma solicitação POST para a API, enviando o texto a ser traduzido e
 * retornando o texto traduzido como resposta.
 * <p>
 * A API FunTranslations suporta diversas categorias de tradução e pode ser usada para traduzir
 * mensagens de forma divertida, com base em diferentes estilos ou categorias, como "pirata", "yoda", etc.
 */
@Component
public class APIFunTranslation {

    public APIFunTranslation(HttpUtil httpUtil) {
        this.httpUtil = httpUtil;
    }

    @Autowired
    HttpUtil httpUtil;

    /**
     * Traduz uma mensagem para a categoria especificada utilizando a API FunTranslations.
     * <p>
     * Esse método faz uma solicitação POST para a API, enviando o texto e a categoria para tradução.
     * Se a tradução for bem-sucedida, o texto traduzido é retornado. Caso contrário, um erro é exibido.
     *
     * @param message  A mensagem a ser traduzida.
     * @param category A categoria para qual a mensagem deve ser traduzida, como "pirate", "yoda", etc.
     * @return O texto traduzido se a solicitação for bem-sucedida, ou `null` em caso de erro.
     */

    public String translateFun(String message, String category) {

        String endPoint = "https://api.funtranslations.com/translate/";
        endPoint += category + ".json";
        String content = String.format("{\"text\":\"%s\"}", message);
        String response = httpUtil.postRequest(endPoint, content);
        if(response != null){
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject contents = jsonResponse.getJSONObject("contents");
            return contents.getString("translated");
        }
        return null;
    }
}
