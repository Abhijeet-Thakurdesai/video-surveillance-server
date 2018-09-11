/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author abhijeet
 */
public class Test {

    public static void main(String[] args) {

        String t = "www.google.com";
        String tt = "192.168.0.105";
        int t1 = 80;                        // HTTP Port No.: 80

        boolean test = checkConnectivityServer(t, t1);
        if (test) {
            System.out.println("INTERNET is ON");
        } else {
            System.out.println("INTERNET is OFF");
        }

    }

    public static boolean checkConnectivityServer(String ip, int port) {
        boolean success = false;
        try {
            Socket soc = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            soc.connect(socketAddress, 3000);
            success = true;
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(" Connecting to server " + success + " IP: " + ip);
        return success;

    }
}
