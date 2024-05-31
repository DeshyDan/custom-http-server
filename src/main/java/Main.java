import connection.ConnectionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {


        try {
            ServerSocket serverSocket = new ServerSocket(4222);
            serverSocket.setReuseAddress(true);

            while (true) {
                System.out.println("Waiting to accept a new connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted a new connection");
                Thread t = new Thread(new ConnectionHandler(clientSocket, args));
                t.start();
            }


        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
