package com.example.snmpreport;

import com.example.snmpreport.utils.Snmpwalk;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScanSnmp {

    public Map<String, String> start(String ipAddr, String commStr, String portNum, String snmpVersion) {
        Map<String, String> resultMap = new HashMap<String, String>();

        try {
            String h = doSnmp(commStr, portNum, snmpVersion, ipAddr, ".1.3.6.1.2.1.1.1");
            if (h != null) {
                String type = detectType(h);
                if (type.equals("unknown")) {
                    h = doSnmp(commStr, portNum, snmpVersion, ipAddr, ".1.3.6.1.2.1.47.1.2.1.1.2");
                    if (h != null) {
                        type = detectType(h);
                    }
                }
                return devideType(type, commStr, portNum, snmpVersion, ipAddr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    private Map<String, String> devideType(String type, String commStr, String portNum, String snmpVerion, String targetAddr) throws IOException {
        String[] result;
        String typeName = null;
        Map<String, String> resultMap = new HashMap<String, String>();
        switch (type) {
            case "sonic":
                result = sonicwallFwSnmp(commStr, portNum, snmpVerion, targetAddr);
                typeName = "SonicWALL Firewall";
                break;
            case "forti":
                result = fortigateFwSnmp(commStr, portNum, snmpVerion, targetAddr);
                typeName = "Fortinet Firewall";
                break;
            case "cisco":
                result = ciscoSwSnmp(commStr, portNum, snmpVerion, targetAddr);
                typeName = "Cisco Router/Switch";
                break;
            case "windows":
                result = windowsSnmp(commStr, portNum, snmpVerion, targetAddr);
                typeName = "Windows";
                break;
            case "linux":
                result = linuxSnmp(commStr, portNum, snmpVerion, targetAddr);
                typeName = "Linux";
                break;
            default:
                result = new String[0];
        }
        if (result.length == 5) {
            resultMap.put("ip", result[0]);
            resultMap.put("hostname", result[1]);
            resultMap.put("model", result[2]);
            resultMap.put("serial", result[3]);
            resultMap.put("version", result[4]);
            resultMap.put("type", typeName);

        }
        return resultMap;
    }


    private String[] fortigateFwSnmp(String commStr, String portNum, String snmpVersion, String targetAddr) throws IOException {
        String hostname = doSnmp(commStr, portNum, snmpVersion, targetAddr, "1.3.6.1.2.1.1.5");
        String serial = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.47.1.1.1.1.11");
        String model = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.47.1.1.1.1.13");
        String version = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.47.1.1.1.1.10");
        return new String[] {targetAddr, hostname, model, serial, version};
    }


    private String[] sonicwallFwSnmp(String commStr, String portNum, String snmpVersion, String targetAddr) throws IOException {
        String hostname = doSnmp(commStr, portNum, snmpVersion, targetAddr, "1.3.6.1.2.1.1.5");
        String serial = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.4.1.8741.2.1.1.2");
        String model = null;
        String version = null;
        String sysDescr = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.1.1");

        if (sysDescr != null) {
            Pattern pattern = Pattern.compile("(.+) \\((.+)\\)");
            Matcher matcher = pattern.matcher(sysDescr);
            if (matcher.matches()) {
                model = matcher.group(1);
                version = matcher.group(2);
            }
        }

        return new String[] {targetAddr, hostname, model, serial, version};
    }


    private String[] ciscoSwSnmp(String commStr, String portNum, String snmpVersion, String targetAddr) throws IOException {
        String hostname = doSnmp(commStr, portNum, snmpVersion, targetAddr, "1.3.6.1.2.1.1.5");
        String serial = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.47.1.1.1.1.11");
        String model = null;
        String version = null;

        String phyDescr = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.47.1.1.1.1.2");
        if (phyDescr != null) {
            Pattern pattern = Pattern.compile("(.+),\\s.+,\\s(.+)");
            Matcher matcher = pattern.matcher(phyDescr);
            if (matcher.matches()) {
                model = matcher.group(1) + " " + matcher.group(2);
            }
        }

        String sysDescr = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.1");
        if (sysDescr != null) {
            String[] sysDescrArray = sysDescr.split(",\\s");
            if (sysDescrArray.length > 3) {
                version = sysDescrArray[1] + " " + sysDescrArray[2];
            }
        }

        return new String[] {targetAddr, hostname, model, serial, version};
    }


    private String[] windowsSnmp(String commStr, String portNum, String snmpVersion, String targetAddr) throws IOException {
        String hostname = doSnmp(commStr, portNum, snmpVersion, targetAddr, "1.3.6.1.2.1.1.5");
        String serial = null;
        String model = null;
        String version = null;

        String sysDescr = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.1.1");
        if (sysDescr != null) {
            Pattern pattern = Pattern.compile("Hardware:\\s(.+)\\s-\\sSoftware:\\s(.+)");
            Matcher matcher = pattern.matcher(sysDescr);
            if (matcher.matches()) {
                version = matcher.group(2);
            }
        }

        return new String[] {targetAddr, hostname, model, serial, version};
    }


    private String[] linuxSnmp(String commStr, String portNum, String snmpVersion, String targetAddr) throws IOException {
        String hostname = doSnmp(commStr, portNum, snmpVersion, targetAddr, "1.3.6.1.2.1.1.5");
        String serial = null;
        String model = null;
        String version = null;

        String sysDescr = doSnmp(commStr, portNum, snmpVersion, targetAddr, ".1.3.6.1.2.1.1.1");
        if (sysDescr != null) {
            Pattern pattern = Pattern.compile("(Linux)\\s" + hostname + "\\s(.+)\\s(.+)$");
            Matcher matcher = pattern.matcher(sysDescr);
            if (matcher.matches()) {
                //model = matcher.group(3);
                version = matcher.group(1) + " " + matcher.group(2);
            }
        }

        return new String[] {targetAddr, hostname, model, serial, version};
    }


    private String detectType(String snmpResult) {
        String[] typeArray = {"sonic", "forti", "cisco", "windows", "linux"};
        String type = "unknown";
        for (String s : typeArray) {
            if (snmpResult.toLowerCase().contains(s)) {
                type = s;
                break;
            }
        }
        return type;
    }


    private String doSnmp(String commStr, String portNum, String snmpVerion, String targetAddr, String oid) throws IOException {
        Snmpwalk snmp = new Snmpwalk();
        String[] snmpArgs = {"-c", commStr, "-p", portNum, "-v", snmpVerion, targetAddr, oid};
        snmp.setArgs(snmpArgs);
        ArrayList<ArrayList<String>> result = snmp.execSnmpwalk();
        if (result.size() > 0) {
            return result.get(0).get(2);
        }
        return null;
    }


    private List<String> generateIpList(String start, String end) {
        String[] startParts = start.split("\\.");
        String[] endParts = end.split("\\.");

        StringBuilder startBin = new StringBuilder();
        StringBuilder endBin = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            String a_str = Integer.toBinaryString(Integer.parseInt(startParts[i]));
            startBin.append(String.join("", Collections.nCopies(8 - a_str.length(), "0"))).append(a_str);
            String b_str = Integer.toBinaryString(Integer.parseInt(endParts[i]));
            endBin.append(String.join("", Collections.nCopies(8 - b_str.length(), "0"))).append(b_str);
        }

        long startLong = Long.parseLong(startBin.toString(), 2);
        long endLong = Long.parseLong(endBin.toString(), 2);

        List<String> result = new ArrayList<String>();
        for (long i=startLong; i <= endLong; i++) {
            String ipAddr = toIpAddr(Long.toBinaryString(i));
            if (ipAddr != null) {
                result.add(ipAddr);
            }
        }
        return result;
    }


    private String toIpAddr(String ip_bin) {
        String pattern = "([0-1]*)([0-1]{8})([0-1]{8})([0-1]{8})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ip_bin);
        if (m.find()) {
            String a = Integer.toString(Integer.parseInt(m.group(1), 2));
            String b = Integer.toString(Integer.parseInt(m.group(2), 2));
            String c = Integer.toString(Integer.parseInt(m.group(3), 2));
            String d = Integer.toString(Integer.parseInt(m.group(4), 2));
            return String.format("%s.%s.%s.%s", a, b, c, d);
        }
        return null;
    }






}

