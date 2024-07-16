package com.example.Job_Application.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private String responseCode;
    private String responseMessage;
    private String email;
    @JsonProperty("access_token")
    private String accessToken;
}
