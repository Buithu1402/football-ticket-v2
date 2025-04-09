package com.app.footballticketservice.utils;

import com.app.footballticketservice.config.SystemConfigService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

@Log
@UtilityClass
public class ImageUploader {
    private static final SystemConfigService SYSTEM_CONFIG_SERVICE = ApplicationContextProvider.getBean(
            SystemConfigService.class);
    private static final String IMGBB_API_KEY = "cba42169003b9e485c7904a85fa0bc3a";

    public static String uploadBase64Image(String base64Image) {
        String apiUrl = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;
        String payload = "image=" + base64Image.replace(Base64Utils.BASE64_PREFIX, "");

        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                var jsonResponse = objectMapper.readValue(response.getBody(), Map.class);

                if (jsonResponse.get("data") instanceof Map<?, ?> dataMap) {
                    if (dataMap.get("url") instanceof String url) {
                        return url;
                    }
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Error parsing JSON response from image upload API", e);
            }
        }
        return null;
    }

    public static String uploadImage(String base64Image) throws IOException {
        var cloudName = SYSTEM_CONFIG_SERVICE.getConfigValue(Constants.CLOUD_NAME);
        var apiKey = SYSTEM_CONFIG_SERVICE.getConfigValue(Constants.API_KEY);
        var apiSecret = SYSTEM_CONFIG_SERVICE.getConfigValue(Constants.API_SECRET);
        var cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
        var uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}
