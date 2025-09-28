package com.ecom.sb_ecom.controller;

import com.ecom.sb_ecom.config.AppConstants;
import com.ecom.sb_ecom.payload.AddressDTO;
import com.ecom.sb_ecom.payload.AddressResponse;
import com.ecom.sb_ecom.payload.ApiResponse;
import com.ecom.sb_ecom.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    AddressService addressService;

    AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        return new ResponseEntity<>(addressService.addAddress(addressDTO), HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> getAllAddresses(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        return new ResponseEntity<>(addressService.getAllAddresses(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddressesByUserId(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder){
        return new ResponseEntity<>(addressService.getAllAddressesByUserId(id, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<AddressResponse> getAddressesByUser(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESS_BY, required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        return new ResponseEntity<>(addressService.getAllAddressesOfLoggedInUser(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable(name = "id") Long addressId, @RequestBody AddressDTO addressDTO) {
        return new ResponseEntity<>(addressService.updateAddress(addressId, addressDTO), HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable(name = "id") Long addressId) {
        return new ResponseEntity<>(addressService.deleteAddress(addressId), HttpStatus.OK);
    }
}
