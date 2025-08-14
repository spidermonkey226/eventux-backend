package com.eventux.backend.repository;

import com.eventux.backend.model.Address;
import com.eventux.backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByCityAndStreetNameIgnoreCaseAndStreetNumberAndPostCode(
            City city, String streetName, String streetNumber, String postCode);
}