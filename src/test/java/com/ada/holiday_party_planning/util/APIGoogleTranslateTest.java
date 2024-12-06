package com.ada.holiday_party_planning.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class APIGoogleTranslateTest {

    @Mock
    HttpUtil mockHttp;

    @InjectMocks
    APIGoogleTranslate apiGoogleTranslate;

    public APIGoogleTranslateTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void translateMessageTest() {
        when(mockHttp.postRequest("https://translation.googleapis.com/language/translate/v2?key=null", "{\"q\":\"Minha mensagem\", \"source\":\"pt-br\", \"target\":\"en\", \"format\":\"text\"}"))
                .thenReturn("{\"data\": {\"translations\": [{\"translatedText\":\"texto traduzido\"}]}}");
        String result = apiGoogleTranslate.translateMessage("Minha mensagem", "pt-br", "en");
        assertEquals("texto traduzido", result);
    }
}