package com.cafe.storekeeper.infrastructure.adapter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.cafe.storekeeper.helper.constant.ConstantsRest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractRestApiClient {

    @Autowired
    private RestTemplate restTemplate;

    public AbstractRestApiClient() {
        super();
    }

    public ResponseEntity<?> getJSON(String url, String token, Class<?> clase) {
        log.info("---| GET EXECUTION |---");
        HttpEntity<String> entity = new HttpEntity<>(this.buildHttpHeaders(token));

        return restTemplate.exchange(url, HttpMethod.GET, entity, clase);
    }

    public ResponseEntity<?> getJSON(String url, String token, Map<String, String> params, Class<?> clase) {
        log.info("---| GET EXECUTION WITH PARAMS |---");
        HttpEntity<String> entity = new HttpEntity<>(this.buildHttpHeaders(token));
        StringBuilder builderURL = new StringBuilder();
        builderURL.append(url);
        if (params != null) {
            builderURL.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builderURL.append(url).append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return restTemplate.exchange(builderURL.toString(), HttpMethod.GET, entity, clase);
    }

    public ResponseEntity<?> postJSON(String url, Object request, String token, Class<?> clase) {
        log.info("---| POST EXECUTION |---");
        HttpEntity<?> entity = this.buildHttpEntity(request, token);
        return restTemplate.exchange(url, HttpMethod.POST, entity, clase);
    }

    public ResponseEntity<?> postJSON(String url, Object request, Class<?> clase) {
        log.info("---| POST EXECUTION NOT TOKEN |---");
        return this.postJSON(url, request, null, clase);
    }

    public ResponseEntity<?> putJSON(String url, Object request, String token, Class<?> clase) {
        log.info("---| PUT EXECUTION |---");
        HttpEntity<?> entity = this.buildHttpEntity(request, token);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, clase);
    }

    public HttpEntity<Object> buildHttpEntity(Object request, String token) {
        HttpHeaders headers = buildHttpHeaders(token);
        return new HttpEntity<>(request, headers);
    }

    public HttpHeaders buildHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isNotBlank(token)) {
            headers.set(ConstantsRest.AUTHORIZATION, ConstantsRest.TOKEN_BASIC + token);
        }
        // Other headers

        return headers;
    }

}