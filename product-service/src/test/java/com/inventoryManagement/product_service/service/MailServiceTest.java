package com.inventoryManagement.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

class MailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    MailService mailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendSimpleMail_invokesSender() {
        mailService.sendSimpleMail("a@x.com", "sub", "body");
        verify(mailSender).send(org.mockito.ArgumentMatchers.any(org.springframework.mail.SimpleMailMessage.class));
    }
}
