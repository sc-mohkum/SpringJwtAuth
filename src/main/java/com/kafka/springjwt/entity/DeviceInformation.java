package com.kafka.springjwt.entity;

import jdk.jfr.DataAmount;
import lombok.Data;

@Data
public class DeviceInformation {

    private String deviceID;
    private String deviceName;
    private String site;
    private Content content;

    @Data
    public class Content {
        private int meterReading;
        private long meterActiveDuration;
    }

}
