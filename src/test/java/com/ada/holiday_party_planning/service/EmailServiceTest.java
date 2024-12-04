package com.ada.holiday_party_planning.service;

import com.ada.holiday_party_planning.model.PartyOwner;
import jakarta.mail.MessageRemovedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private PartyOwner mockPartyOwner;

    @BeforeEach
    void setUp() {
        mockPartyOwner = new PartyOwner();
        mockPartyOwner.setName("Alan");
        mockPartyOwner.setEmail("alan@test");

        emailService = new EmailService(javaMailSender);
        emailService.from = "holydaypartyplanner@gmail.com";
    }

    @Test
    void dadoEmailValido_quandoEnviarEmail_entaoRetornaSucesso() throws Exception {

        // dado
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        doNothing().when(javaMailSender).send(mimeMessage);

        Map<String, String> variables = new HashMap<>();
        variables.put("Key", "Value");

        // quando
        emailService.sendEmail(mockPartyOwner.getEmail(), "Test Subject", variables);

        // então
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void dadoEmailInvalido_quandoEnviarEmail_entaoRetornaFalha() {

        // dado
        String invalidEmail = "invalid-email";

        // quando e então
        Map<String, String> variables = new HashMap<>();
        variables.put("Key", "Value");

        assertThrows(Exception.class, () -> {
            emailService.sendEmail(invalidEmail, "Test Subject", variables);
        });
    }

    @Test
    void dadoEmailNulo_quandoEnviarEmail_entaoLancaExcecao() {

        // dado
        String nullEmail = null;

        // quando e então
        Map<String, String> variables = new HashMap<>();
        variables.put("Key", "Value");

        assertThrows(MessagingException.class, () -> {
            emailService.sendEmail(nullEmail, "Test Subject", variables);
        });
    }

}


