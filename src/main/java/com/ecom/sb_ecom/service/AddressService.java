package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.payload.AddressDTO;
import com.ecom.sb_ecom.payload.AddressResponse;
import com.ecom.sb_ecom.payload.ApiResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO);
    AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    AddressResponse getAllAddressesByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    AddressResponse getAllAddressesOfLoggedInUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    @Transactional
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    @Transactional
    ApiResponse deleteAddress(Long addressId);
}
