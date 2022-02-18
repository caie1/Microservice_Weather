package com.example.details.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface WeatherService {
    List<Integer> findCityIdByName(String city);
    Map<String, Map> findCityNameById(int id);
    CompletableFuture<List<Map<String, Map>>> findCitiesNameByIds(List<Integer> ids);
}
