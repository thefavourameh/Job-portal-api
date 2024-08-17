package com.example.Job_Application.payload.response;

import com.example.Job_Application.entities.ErrorDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserResponse<T> {
    private String responseMessage;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String curriculumVitae;
    private T data;

    public UserResponse(String responseMessage, Long id, String firstName, String lastName, String email, String dateOfBirth, String curriculumVitae,  T data) {
        this.responseMessage = responseMessage;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.curriculumVitae = curriculumVitae;
        this.data = data;
    }

    public UserResponse(String responseMessage, ErrorDetails errorDetails) {
        this.responseMessage = responseMessage;
    }

    public UserResponse(String message, String fileUrl) {
        this.responseMessage = message;
        this.data = (T) fileUrl;
    }

    public UserResponse(String message) {
        this.responseMessage = message;
    }

    public String getData() {
        return data != null ? data.toString() : null;
    }
}

