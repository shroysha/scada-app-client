package dev.shroysha.scada.app.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class DummyClient {


    public static void main(String[] args) {
        BufferedReader in = null;

        try {
            Socket scadaConnection = new Socket("localhost", 10000);
            PrintWriter out = new PrintWriter(scadaConnection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(scadaConnection.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
            System.exit(1);
        }

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                System.out.println(in.readLine());
            } catch (IOException ignored) {

            }
        }
    }
}
