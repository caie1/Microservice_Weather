package com.example.details.pojo;

import lombok.Data;

@Data
public class City {
    private Integer woeid;

    public Integer getWoeid() {
        return woeid;
    }

    public void setWoeid(Integer woeid) {
        this.woeid = woeid;
    }
}
