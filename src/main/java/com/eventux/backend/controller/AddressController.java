package com.eventux.backend.controller;

import com.eventux.backend.model.Address;
import com.eventux.backend.service.AddressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresss")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<Address> getAll() {
        return addressService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Address> getById(@PathVariable int id) {
        return addressService.getById(id);
    }

    @PostMapping
    public Address create(@RequestBody Address obj) {
        return addressService.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        addressService.deleteById(id);
    }
}