package com.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(9090));
        socket.connect(new InetSocketAddress("localhost",8888));


    }
}
