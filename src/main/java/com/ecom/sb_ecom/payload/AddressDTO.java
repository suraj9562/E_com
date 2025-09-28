package com.ecom.sb_ecom.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;

    @NotBlank
    @Size(min = 5, message = "street name must be of at least 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "building name must be of at least 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 2, message = "city name must be of at least 2 characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "state name must be of at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "country name must be of at least 2 characters")
    private String country;

    @NotBlank
    @Size(min = 5, message = "zip code must be of at least 6 characters")
    private String zipcode;
}
