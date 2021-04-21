package com.example.snmpreport.models;

public class Device {
    private final String ip;
    private final String hostname;
    private final String model;
    private final String serial;
    private final String os;
    private final String type;

    public Device(String ip, String hostname, String model, String serial, String os, String type) {
        this.ip = ip;
        this.hostname = hostname;
        this.model = model;
        this.serial = serial;
        this.os = os;
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public String getHostname() {
        return hostname;
    }

    public String getModel() {
        return model;
    }

    public String getSerial() {
        return serial;
    }

    public String getOs() {
        return os;
    }

    public String getType() {
        return type;
    }
}
