package com.example.Job_Application.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private final String CLOUD_NAME = "dxrcup0yc";
    private final String API_KEY = "826545725491842";
    private final String API_SECRET = "_s2ug0vsxBahVlRsPgaOoCIwj5A";

    @Bean
    Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);

        return new Cloudinary(config);
    }
}
