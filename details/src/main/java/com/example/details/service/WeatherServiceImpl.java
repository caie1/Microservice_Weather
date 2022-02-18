package com.example.details.service;



import com.example.details.config.EndpointConfig;
import com.example.details.pojo.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService{

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;


    public WeatherServiceImpl(RestTemplate getRestTemplate) {
        this.restTemplate = getRestTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Integer> findCityIdByName(String city) {
        City[] cities = restTemplate.getForObject(EndpointConfig.queryWeatherByCity + city, City[].class);
        List<Integer> ans = new ArrayList<>();
        for(City c: cities) {
            if(c != null && c.getWoeid() != null) {
                ans.add(c.getWoeid());
            }
        }
        return ans;
    }

    @Override
    //change findcitynamebyid => find weather details by id
    public Map<String, Map> findCityNameById(int id) {
        Map<String, Map> ans = restTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        return ans;
    }

    @Override
    @Async
    public CompletableFuture<List<Map<String, Map>>> findCitiesNameByIds(List<Integer> ids) {
        LOGGER.info("Request to get a list of weathers");
        List<CompletableFuture<Map<String, Map>>> all_f = new ArrayList<>();
        for (int id : ids){
            CompletableFuture<Map<String, Map>> f = CompletableFuture.completedFuture(id)
                    .thenApply(s -> this.findCityNameById(s));
            all_f.add(f);

        }

        CompletableFuture<List<Map<String, Map>>> cf = CompletableFuture.allOf(all_f.toArray(new CompletableFuture[all_f.size()]))
                .thenApply(f -> {
                    return all_f.stream().map(future -> future.join())
                            .collect(Collectors.toList());
                });

        return cf.toCompletableFuture();
    }

}
