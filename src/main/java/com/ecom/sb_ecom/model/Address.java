package com.ecom.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private long addressId;

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
    @Size(min = 6, message = "zip code must be of at least 6 characters")
    private String zipCode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();
}
