package com.eventux.backend.controller;

import com.eventux.backend.model.City;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    @GetMapping
    public List<Map<String, String>> getAllCities() {
        return Arrays.stream(City.values())
                .map(c -> Map.of("key", c.name(), "label", c.getValue()))
                .toList();
    }
}


