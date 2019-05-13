package com.example.bio;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DataReceiver {
    public static Thread udpThread;
    private static DatagramSocket dsocket;
    private static DatagramPacket packet;
    private static String lText;
    private static byte[] buffer;

    public static void startUDPServer() throws IOException {
        dsocket = new DatagramSocket(Constants.MOB_PORT_NUMBER);
        buffer = new byte[1024];
        packet = new DatagramPacket(buffer, buffer.length);

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };


        udpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        dsocket.receive(packet);
                    } catch (IOException e) {
                        e.getMessage();
                        throw new RuntimeException();
                    }

                    Constants.INET_ADDRESS = packet.getAddress();
                    Log.d("Address of rasp", Constants.INET_ADDRESS.getCanonicalHostName());

                    lText = new String(buffer, 0, packet.getLength());
                    Log.i("UDP packet received", lText);
//                    if(lText.equals())
                    DataBuffer.MSG_QUEUE.add(lText);

                    packet.setLength(buffer.length);
                }

            }
        });
        udpThread.setUncaughtExceptionHandler(h);
        udpThread.start();

    }

    public static void sendPacket(String msg) throws SocketException, UnknownHostException, IOException {
        byte[] buff = new byte[1024];
        final DatagramSocket ds = new DatagramSocket(Constants.PY_PORT_NUMBER);
        buff = msg.getBytes();
        InetAddress inetAddress = Constants.INET_ADDRESS;
        final DatagramPacket dp = new DatagramPacket(buff, buff.length, inetAddress, Constants.PY_PORT_NUMBER);

        Log.d("sendPacket", "sending msg" +msg);

        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    ds.send(dp);
                    Log.d("sendPacket","in thread");
                }
                catch (Exception e){
                    Log.d("Error", e.getMessage());
                }
            }
        });

        t.setUncaughtExceptionHandler(h);
        t.start();
    }
}

