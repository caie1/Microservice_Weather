package com.example.details.controller;

import com.example.details.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@RestController
public class WeatherController {


    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;


    @Value("${server.port}")
    private int randomServerPort;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/details")
    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) String city) {
        LOGGER.info("get" + city + " weather");
        return new ResponseEntity<>(weatherService.findCityIdByName(city), HttpStatus.OK);
    }


    @GetMapping("/details/{id}")
    public ResponseEntity<?> queryWeatherByCity(@PathVariable int id) {
        LOGGER.info("get" + id + " weather");
        return new ResponseEntity<Map>(weatherService.findCityNameById(id), HttpStatus.OK);
    }

    @GetMapping("/details/port")
    public ResponseEntity<?> queryWeatherByCity() {
        LOGGER.info("get weather from randomServerPort");
        return new ResponseEntity<>("weather service + " + randomServerPort, HttpStatus.OK);
    }

    @GetMapping("/details/{ids}")
    public CompletableFuture<ResponseEntity> queryWeatherByCity(@PathVariable List<Integer> ids) {
        LOGGER.info("get" + ids + " weather");
        return weatherService.findCitiesNameByIds(ids)
                .<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleGetCitesFailure);

    }

    private static Function<Throwable, ResponseEntity<?>> handleGetCitesFailure = throwable ->{
        LOGGER.error("Failed to get all cities: {}", throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
}
