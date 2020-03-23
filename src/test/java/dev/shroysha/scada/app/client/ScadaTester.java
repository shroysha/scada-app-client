package dev.shroysha.scada.app.client;

import net.wimpi.modbus.facade.ModbusTCPMaster;
import net.wimpi.modbus.util.BitVector;

import java.net.InetAddress;


public class ScadaTester {


    public static void main(String[] args) throws Exception {
        InetAddress IP = InetAddress.getByName("192.168.41.30");
        ModbusTCPMaster mbm = new ModbusTCPMaster(IP.getHostAddress(), 502);
        System.out.println("About to connect");
        mbm.connect();
        System.out.println("Connected");
        BitVector bv = mbm.readInputDiscretes(1, 1);
        System.out.println(bv.getBit(0));
        mbm.disconnect();
    }
}
