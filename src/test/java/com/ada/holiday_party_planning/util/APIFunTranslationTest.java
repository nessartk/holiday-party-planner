package com.ada.holiday_party_planning.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class APIFunTranslationTest {

    @Mock
    HttpUtil mockHttp;

    @InjectMocks
    APIFunTranslation apiFunTranslation;

    public APIFunTranslationTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void translateFunTest() {
        when(mockHttp.postRequest("https://api.funtranslations.com/translate/yoda.json", "{\"text\":\"Minha mensagem\"}"))
                .thenReturn("{\"contents\": {\"translated\": \"Mensagem minha\"}}");
        String result = apiFunTranslation.translateFun("Minha mensagem", "yoda");
        assertEquals("Mensagem minha", result);
    }
}