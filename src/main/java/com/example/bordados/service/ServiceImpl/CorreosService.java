package com.example.bordados.service.ServiceImpl;
/* 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
*/
public class CorreosService {

    /*private final String apiKey = "TU_API_KEY";
    private final String apiUrl = "https://api.correos.es/tracking";

    @Autowired
    private RestTemplate restTemplate;

    public String getTrackingStatus(String trackingNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = apiUrl + "?trackingNumber=" + trackingNumber;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al obtener el estado del seguimiento: " + response.getStatusCode());
        }
    }*/
}