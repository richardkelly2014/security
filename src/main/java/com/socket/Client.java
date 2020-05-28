package com.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {

        for (int i = 0; i < 256; i++) {
            Socket socket = new Socket();
            socket.setReuseAddress(true);
            //socket.setSoLinger(true, 1);
            //socket.bind(new InetSocketAddress(9090));
            socket.connect(new InetSocketAddress("localhost", 8888));
        }
//        Thread.sleep(5000);
//        socket.close();

        //Thread.sleep(5000);
        System.in.read();
    }
}
