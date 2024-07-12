/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author skaya
 */
public class ServerClient extends Thread { // Player

    Server server;
    Socket socket;
    OutputStream output;
    InputStream input;
    boolean isListening;
    Game game;
    boolean isInGame;
    int score;
    ServerClient rival;
    boolean isMyTurn;

    public ServerClient(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.isListening = false;
        this.isInGame = false;
        this.score = -1;
        this.isMyTurn = false;
        //System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> connected");
        //Frm_Server.lst_clients_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort());
    }

    public void Start() {
        isListening = true;
        start();
    }

    public void Stop() {
        try {
            isListening = false;
            output.close();
            input.close();
            socket.close();
            server.RemoveClient(this);

        } catch (IOException ex) {
            System.out.println("kapatma hatası");
            System.out.println(socket.getInetAddress().toString() + ":" + socket.getPort() + "-> stoped");

        }
    }

    public void SendMessage(byte[] messageBytes) throws IOException {
        this.output.write(messageBytes);
    }

    @Override
    public void run() {
        try {
            while (isListening) {

                int byteSize = input.read(); //blocking
                byte bytes[] = new byte[byteSize];
                input.read(bytes);
                String s = new String(bytes, StandardCharsets.UTF_8);
                System.out.println(this.socket.getInetAddress().toString() + ":" + socket.getPort() + "-> message reacted serverclient");
                System.out.print(s);

                if (s.charAt(0) == 'p') {       // if client clicked the play button

                    if (!server.readyClients.contains(this)) {
                        server.readyClients.add(this);
                    }
                    if (server.readyClients.size() % 2 == 0) {
                        server.CreateGame();
                    }

                    //Frm_Server.lst_messagesFromClient_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "->" + new String(bytes, StandardCharsets.UTF_8));
// JUST FOR SEE THE RESULTS
                    System.out.println("");
                    // ArrayList'i tek bir satırda yazdır
                    System.out.print("readyclients:  ");
                    System.out.print("[ ");
                    for (int i = 0; i < server.readyClients.size(); i++) {
                        System.out.print(server.readyClients.get(i).socket.getPort());
                        if (i != server.readyClients.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print("  ]");

                    // ArrayList'i tek bir satırda yazdır
                    System.out.print("clients:  ");
                    System.out.print("[ ");
                    for (int i = 0; i < server.clients.size(); i++) {
                        System.out.print(server.clients.get(i).socket.getPort());
                        if (i != server.clients.size() - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.print("  ]");
                    System.out.println("");

                }
                if (s.charAt(0) == 'I') {             //  IQuit message from other player in game
                    isInGame = false;
                    server.readyClients.remove(this);
                    server.readyClients.remove(this.rival);
                    this.rival.SendMessage("_leave".getBytes());
                }

                if (s.charAt(0) == 'm') {             // "myTurnOver" message  // //  Score of the player message
//                    Runnable delayedTask = () -> {
                    this.rival.isMyTurn = true;
                    this.isMyTurn = false;
//                    };
//                    Thread thread = new Thread(delayedTask);
//                    thread.start();



                    int receivedInt;
                    if ((s.charAt(2)) == '0') {
                        receivedInt = (s.charAt(3) - 48);
                        System.out.println("received int " + receivedInt);
                    } else {
                        receivedInt = (((s.charAt(2) - 48) * 10) + (s.charAt(3) - 48));
                        System.out.println("received int " + receivedInt);
                    }
                    this.score = receivedInt;
                    System.out.println("receivedInt == " + receivedInt);
                    
                    
                    // set strings number as 03 , 07
                    String receivedS = String.valueOf(receivedInt);
                    if (receivedS.length() == 1) {
                        receivedS = ("0" + receivedS);
                    }
                    this.rival.SendMessage(("_S_" + receivedS + "").getBytes());      // send score to your rival 

                }

                if (s.charAt(0) == 'f') {             // "myTurnOver" message  // //  Score of the player message
                    
                    System.out.println("oyun bitti uyarısı geldi ");
                    this.game.isOver = true;
                    this.SendMessage("_l".getBytes());   // send game isover 
                    this.rival.SendMessage("_l".getBytes());   // send game isover 

                }

//                Runnable delayedTask = () -> {
//                    try {
//                        int receivedNumber = input.readInt();
//                    } catch (IOException ex) {
//                        Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                };
//                Thread thread = new Thread(delayedTask);
//                thread.start();
            }

        } catch (IOException ex) {
            this.Stop();
            System.out.println(socket.getInetAddress().toString() + ":" + socket.getPort() + "->  ");
            //Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NegativeArraySizeException ex) {
            this.Stop();
            server.RemoveClient(this);
        }
    }

}
