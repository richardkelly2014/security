package com.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);

        //bind
        serverSocket.bind(new InetSocketAddress(8888));

        // 等待客户端连接
        Socket client = serverSocket.accept();

        InetAddress clientAddress = client.getInetAddress();

        System.out.println(clientAddress.toString() + ":" + client.getPort() + "::" + client.getLocalPort());
    }

}
