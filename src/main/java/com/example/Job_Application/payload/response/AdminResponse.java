package com.example.Job_Application.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AdminResponse {
    private String responseMessage;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String identityNumber;

}
