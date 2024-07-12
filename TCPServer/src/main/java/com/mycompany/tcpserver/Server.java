/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author skaya
 */
public class Server extends Thread {

    int port;
    TCPServer serverWindow;
    ServerSocket serverSocket;
    boolean isListening;
    ArrayList<ServerClient> clients;
    ArrayList<ServerClient> readyClients; // this is using just hold 2 ready player only
    ArrayList<Game> games;

    //Thread listenThread;
    public Server(int port, TCPServer serverWindow) {
        try {
            this.port = port;
            this.serverWindow = serverWindow;
            this.serverSocket = new ServerSocket(port);
            this.isListening = false;
            this.clients = new ArrayList<>();
            this.readyClients = new ArrayList<>();
            this.games = new ArrayList<>();

            //this.listenThread= new Thread();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Listen() {
        this.isListening = true;
        this.start();
        System.out.println("Server startted...");

    }

    public void Stop() {
        this.isListening = false;
        try {
            serverSocket.close();
            for (ServerClient player : clients) {
                player.socket.close();
                RemoveClient(player);
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
        }

    }

    public void AddClient(ServerClient serverClient) {
        this.clients.add(serverClient);
        System.out.println("Client Added...");
        //Frm_Server.lst_clients_model.addElement(serverClient.socket.getInetAddress().toString() + ":" + serverClient.socket.getPort());

    }

    public void RemoveClient(ServerClient serverClient) {
        this.clients.remove(serverClient);
        System.out.println("Client removed...");
        if (readyClients.contains(serverClient)) {
            readyClients.remove(serverClient);
        }

        serverWindow.lst_clients_model.removeAllElements();
        for (int i = 0; i < clients.size(); i++) {
            serverWindow.lst_clients_model.addElement("player  " + clients.get(i).socket.getPort());
        }

        System.out.print("clients:  ");
        System.out.print("[ ");
        for (int i = 0; i < clients.size(); i++) {
            System.out.print(clients.get(i).socket.getPort());
            if (i != clients.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("");

        /*
        lst_clients_model.removeAllElements();
        for (ServerClient client : clients) {
            Frm_Server.lst_clients_model.addElement(client.socket.getInetAddress().toString() + ":" + client.socket.getPort());
            break;
        }
         */
    }

    public ServerClient GetClientByIndex(int index) {
        return this.clients.get(index);
    }

    public void SendBroadcast(String message) throws IOException {
        byte[] bytes = (" " + message).getBytes();
        for (ServerClient client : clients) {
            client.SendMessage(bytes);
        }
        System.out.println("Broadcast message send...");
    }

    public void SendToClient(String message, int index) throws IOException {
        byte[] bytes = (" " + message).getBytes();
        this.clients.get(index).SendMessage(bytes);
        System.out.println("Client message send...");
    }

    public void CreateGame() throws IOException {
        Game g = new Game(readyClients.get(0), readyClients.get(1), this);
        g.start();
        if (games.contains(g)) {
            games.add(g);
        }
        readyClients.clear();
        

        serverWindow.lst_games_model.removeAllElements();
        for (int i = 0; i < games.size(); i++) {
            serverWindow.lst_games_model.addElement("game " + i);
        }
        
    }

    public static void main(String[] args) {
        TCPServer serverScreen = new TCPServer();
        serverScreen.setVisible(true);
    }

    public void run() {

        while (this.isListening) {
            try {
                Socket clientSocket = this.serverSocket.accept(); //blocking
                System.out.println("Client connected...");
                ServerClient player = new ServerClient(this, clientSocket);
                this.AddClient(player);
                player.Start();
                player.SendMessage(("_merhaba:" + player.socket.getInetAddress().toString() + ":" + player.socket.getPort()).getBytes());

                // ArrayList'i tek bir satırda yazdır
                System.out.print("clients:  ");
                System.out.print("[ ");
                for (int i = 0; i < clients.size(); i++) {
                    System.out.print(clients.get(i).socket.getPort());
                    if (i != clients.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("  ]");
                System.out.println("");

                serverWindow.lst_clients_model.removeAllElements();
                for (int i = 0; i < clients.size(); i++) {
                    serverWindow.lst_clients_model.addElement("player  " + clients.get(i).socket.getPort());
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
