package com.example.Job_Application.config.cloudinaryConfig;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    private final String CLOUD_NAME = "dt4huhanq";
    private final String API_KEY = "889728678356158";
    private final String API_SECRET = "5tSNPuK6grJ3xSylTVNCGxg8PJY";


    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);

        return new Cloudinary(config);
    }

}
