package com.ecom.sb_ecom.security.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("username")
    @NotEmpty
    @Size(max = 20, message = "Size of username must be less than 20")
    private String username;

    @JsonProperty("password")
    @NotEmpty
    @Size(max = 120, message = "Size of password must be less than 120")
    private String password;
}
