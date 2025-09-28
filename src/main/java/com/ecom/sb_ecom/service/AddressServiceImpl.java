package com.ecom.sb_ecom.service;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecom.sb_ecom.model.Address;
import com.ecom.sb_ecom.model.User;
import com.ecom.sb_ecom.payload.AddressDTO;
import com.ecom.sb_ecom.payload.AddressResponse;
import com.ecom.sb_ecom.payload.ApiResponse;
import com.ecom.sb_ecom.repository.AddressesRepository;
import com.ecom.sb_ecom.repository.UserRepository;
import com.ecom.sb_ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.TypeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressesRepository addressesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper = new ModelMapper();

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = authUtil.getLoggerInUser();

        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addresses = user.getAddresses();

        addresses.add(address);
        user.setAddresses(addresses);

        address.setUser(user);
        Address savedAddress = addressesRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressResponse getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Address> addressPage = addressesRepository.findAll(pageable);
        List<Address> addresses = addressPage.getContent();

        AddressResponse addressResponse = new AddressResponse();
        List<AddressDTO> addressDTOS = addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class)
        ).toList();

        addressResponse.setAddressDtosList(addressDTOS);
        addressResponse.setTotalElements(addressPage.getTotalElements());
        addressResponse.setPageNumber(addressPage.getNumber());
        addressResponse.setPageSize(addressPage.getSize());
        addressResponse.setTotalPages(addressPage.getTotalPages());
        addressResponse.setLastPage(addressPage.isLast());
        return addressResponse;
    }

    @Override
    public AddressResponse getAllAddressesByUserId(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", userId)
        );

        Sort sort = sortBy.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Address> addressPage = addressesRepository.findByUserId(userId, pageable);
        List<Address> addresses = addressPage.getContent();

        AddressResponse addressResponse = new AddressResponse();
        List<AddressDTO> addressDTOS = addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class)
        ).toList();

        addressResponse.setAddressDtosList(addressDTOS);
        addressResponse.setTotalElements(addressPage.getTotalElements());
        addressResponse.setPageNumber(addressPage.getNumber());
        addressResponse.setPageSize(addressPage.getSize());
        addressResponse.setTotalPages(addressPage.getTotalPages());
        addressResponse.setLastPage(addressPage.isLast());
        return addressResponse;
    }

    public AddressResponse getAllAddressesOfLoggedInUser(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        User user = authUtil.getLoggerInUser();
        return getAllAddressesByUserId(user.getUserId(), pageNumber, pageSize, sortBy, sortOrder);
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO){
        Address addressFromDb = addressesRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId)
        );

        User user = authUtil.getLoggerInUser();
        List<Address> addresses = user.getAddresses();
        addresses.remove(addressFromDb);

        Address address = modelMapper.map(addressDTO, Address.class);
        addressFromDb.setBuildingName(addressFromDb.getBuildingName());
        addressFromDb.setStreet(address.getStreet());
        addressFromDb.setCity(address.getCity());
        addressFromDb.setState(address.getState());
        addressFromDb.setZipcode(address.getZipcode());
        addressFromDb.setCountry(address.getCountry());

        Address savedAddress = addressesRepository.save(addressFromDb);
        addresses.add(savedAddress);
        userRepository.save(user);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Transactional
    public ApiResponse deleteAddress(Long addressId){
        Address addressFromDb = addressesRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId)
        );

        User user = authUtil.getLoggerInUser();
        List<Address> addresses = user.getAddresses();
        addresses.remove(addressFromDb);
        userRepository.save(user);

        addressesRepository.delete(addressFromDb);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Address is deleted from database");
        apiResponse.setStatus(true);

        return apiResponse;
    }
}
