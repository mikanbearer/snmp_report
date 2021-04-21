package com.example.snmpreport.controllers;

import com.example.snmpreport.models.Device;
import com.example.snmpreport.ScanSnmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class GetDeviceInfoController {

    private static final Logger log = LoggerFactory.getLogger(GetDeviceInfoController.class);
    private static final ScanSnmp scanSnmp = new ScanSnmp();
    private static final String logTemplate = "snmpwalk [communityName: %s, portNumber: %s, snmpVersion: %s, targetAddr: %s]";

    @CrossOrigin(origins = "*")
    @GetMapping("/get")
    public Device getDiveceInfo(
            @RequestParam(value = "ip") String ip,
            @RequestParam(value = "c", defaultValue = "public") String c,
            @RequestParam(value = "p", defaultValue = "161") String p,
            @RequestParam(value = "v", defaultValue = "2") String v
    ) {
        log.info(String.format(logTemplate, c, p, v, ip));
        Map<String, String> resultMap = scanSnmp.start(ip, c, p, v);
        return new Device(resultMap.get("ip"), resultMap.get("hostname"), resultMap.get("model"), resultMap.get("serial"), resultMap.get("version"), resultMap.get("type"));
    }
}
