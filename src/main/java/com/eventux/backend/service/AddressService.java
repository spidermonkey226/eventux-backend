package com.eventux.backend.service;

import com.eventux.backend.model.Address;
import com.eventux.backend.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAll() {
        return addressRepository.findAll();
    }

    public Optional<Address> getById(int id) {
        return addressRepository.findById(id);
    }

    public Address save(Address obj) {
        return addressRepository.save(obj);
    }

    public void deleteById(int id) {
        addressRepository.deleteById(id);
    }
}
