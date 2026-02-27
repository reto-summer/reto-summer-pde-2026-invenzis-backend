package com.example.reto_backend_febrero2026.notificacion.whatsapp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsappMetaService implements IWhatsappMetaService {

    private final RestTemplate restTemplate;
    private final String apiVersion;
    private final String phoneNumberId;
    private final String accessToken;
    private final String to;

    public WhatsappMetaService(
            @Value("${whatsapp.meta.api-version:v21.0}") String apiVersion,
            @Value("${whatsapp.meta.phone-number-id:}") String phoneNumberId,
            @Value("${whatsapp.meta.access-token:}") String accessToken,
            @Value("${whatsapp.meta.to:}") String to) {
        this.restTemplate = new RestTemplate();
        this.apiVersion = apiVersion;
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.to = to;
    }

    @Override
    public void sendTextMessage(String message) {
        validateConfiguration();

        String url = "https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", message)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Meta WhatsApp respondió con estado no exitoso: " + response.getStatusCode());
        }
    }

    private void validateConfiguration() {
        if (isBlank(apiVersion) || isBlank(phoneNumberId) || isBlank(accessToken) || isBlank(to)) {
            throw new IllegalStateException("Falta configuración de WhatsApp Meta (api-version, phone-number-id, access-token o to)");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}