package com.example.snmpreport.utils;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Snmpwalk {
    private String targetAddr;
    private String oidStr;
    private String commStr;
    private int snmpVersion;
    private String portNum;
    private String usage;

    public Snmpwalk() {
        // Set default value.
        targetAddr = null;
        oidStr = null;
        commStr = "public";
        snmpVersion = SnmpConstants.version2c;
        portNum =  "161";
        usage = "Usage: snmpwalk [-c communityName -p portNumber -v snmpVersion(1 or 2)] targetAddr oid";
    }

    public ArrayList<ArrayList<String>> execSnmpwalk() throws IOException {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Address targetAddress = GenericAddress.parse("udp:"+ targetAddr + "/" + portNum);
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        try {
            transport.listen();

            // setting up target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(commStr));
            target.setAddress(targetAddress);
            target.setRetries(3);
            target.setTimeout(1000);
            target.setVersion(snmpVersion);
            OID oid = new OID(oidStr);

            // Get MIB data.
            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(target, oid);
            if (events == null || events.size() == 0) {
                System.err.println("No result returned.");
                System.exit(1);
            }

            // Handle the snmpwalk result.
            for (TreeEvent event : events) {

                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    continue;
                }

                VariableBinding[] varBindings = event.getVariableBindings();
                if (varBindings == null || varBindings.length == 0) {
                    continue;
                }
                for (VariableBinding varBinding : varBindings) {
                    if (varBinding == null) {
                        continue;
                    }
                    ArrayList<String> temp = new ArrayList<String>();

                    temp.add(varBinding.getOid().toString());
                    temp.add(varBinding.getVariable().getSyntaxString());
                    temp.add(varBinding.getVariable().toString());

                    result.add(temp);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            snmp.close();
        }
        return result;
    }


    public void setArgs(String[] args) {
        if(args.length < 2) {
            System.err.println(usage);
            System.exit(1);
        }

        for (int i=0; i<args.length; i++) {
            if("-c".equals(args[i])) {
                commStr = args[++i];
            }
            else if ("-v".equals(args[i])) {
                if(args[++i].equals('1')) {
                    snmpVersion = SnmpConstants.version1;
                }
                else {
                    snmpVersion = SnmpConstants.version2c;
                }
            }
            else if ("-p".equals(args[i])) {
                portNum = args[++i];
            }
            else{
                targetAddr = args[i++];
                oidStr = args[i];
            }
        }
        if(targetAddr == null || oidStr == null) {
            System.err.println(usage);
            System.exit(1);
        }
    }
}