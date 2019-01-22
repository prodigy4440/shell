package com.fahdisa.shell;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class Telnet {

    public Boolean isReachable(String host, Integer port){
        TelnetClient telnetClient = new TelnetClient();
        try {
            telnetClient.connect(host, port);
            telnetClient.disconnect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String atrs []){
        for (int i =0; i < 30; i++){
            System.out.println(new Telnet().isReachable("192.168.8.104",36787));
        }
    }

}
