/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tcpclientant;

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
public class Client extends Thread { // Player

    TCPClient clientWindow;
    GameScreen gameScreen;
    Socket socket;
    OutputStream output;
    InputStream input;
    boolean isListening;
//    Game game;
    boolean isInGame;
    int score;

    public Client(Socket socket, TCPClient clientWindow) throws IOException {
        this.clientWindow = clientWindow;
        this.gameScreen = new GameScreen();

        // connect mainscreen and gamescreen
        this.gameScreen.mainScreen = clientWindow;

        this.socket = socket;
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.isListening = false;
        this.isInGame = false;
        this.score = 0;
        //System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> connected");
        //Frm_Server.lst_clients_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort());

    }

    public void Start() {
        this.isListening = true;
        this.start();
    }

    public void Stop() {
        try {
            this.isListening = false;
            this.output.close();
            this.input.close();
            this.socket.close();

        } catch (IOException ex) {
            System.out.println("kapatma hatası");
            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> stoped");

        }
    }

    public void SendMessage(byte[] messageBytes) throws IOException {
        this.output.write(messageBytes);
        this.output.flush();
    }

    public void GetInToGame() throws IOException {
        isInGame = true;

        if (isInGame == true) {
            clientWindow.setVisible(false);
        }
        gameScreen.show();
        gameScreen.client = this;
    }

    public static void main(String[] args) throws IOException {
        TCPClient main = new TCPClient();
        main.setVisible(true);
//        System.out.println('4' - 48);
    }

    @Override
    public void run() {
        try {
            System.out.println("client calismaya basladi ");
            while (this.isListening) {

                // NOT :
                // Tam stringi bulmak için gönderirken gönderdiğin datanın uzunluğunuda yaz  Örnek : "_2_go"
                // byteSize'ı bulduktan sonra hesaplama yaparak tam stringe ulaş
                int byteSize = input.read(); //blocking     
                byte bytes[] = new byte[byteSize];
                input.read(bytes);
                String s = new String(bytes, StandardCharsets.UTF_8);

                if (s.charAt(0) == 'g' && s.charAt(1) != 'o') { // getInToGame message
                    GetInToGame();
                    System.out.println("getintogame");
                }
                if (s.charAt(0) == 'g' && s.charAt(1) == 'o') { // go (it is your turn) message
                    gameScreen.btn_roll.setEnabled(true);
                    System.out.println("go");
                }
                if (s.charAt(0) == 'w' && s.charAt(1) == 'a') { // wait (it is not your turn) message
                    gameScreen.btn_roll.setEnabled(false);
                    System.out.println("wait");
                }

                // if one of the client leave early
                if (s.charAt(0) == 'l') {                              // leaveTheGame message
                    
                    this.gameScreen.actionMessage.setText("Your rival is quitted or game is ended up !!! //n"
                            + "You will be directed main menu in 5 sec");
                    Thread.sleep(5000);
                    this.gameScreen.setVisible(false);
                    System.out.println("leaved the game ");
                    SendMessage("_reset".getBytes());
                    // reset main window
                    this.clientWindow.setVisible(true);
                    clientWindow.searching.setText("Player waiting...");
                    clientWindow.counter.setText("0 sn");
                    clientWindow.searching.setVisible(false);
                    clientWindow.counter.setVisible(false);
                    clientWindow.playButton.setEnabled(true);
                    
                    
                    
                    gameScreen.actionMessage.setText("");
                    gameScreen.btn_roll.setEnabled(true);
                    gameScreen.lbl_playerS.setText("");
                    gameScreen.lbl_rivalS.setText("");
                    gameScreen.lbl_dicee.setText("");
                    this.score =0;
                }

                if (s.charAt(0) == 'S') {                                   // take the rivals score
                    char firstDigit = s.charAt(2);
                    char secondDigit = s.charAt(3);
                    int receivedInt;
                    if ((s.charAt(2)) == '0') {
                        receivedInt = (s.charAt(3) - 48);
                        System.out.println("received int " + receivedInt);
                    } else {
                        receivedInt = (((s.charAt(2) - 48) * 10) + (s.charAt(3) - 48));
                        System.out.println("received int " + receivedInt);
                    }

                    this.gameScreen.lbl_rivalS.setText("Your rival's  score is " + receivedInt + "");
                    if (receivedInt >= 100) {
                        this.SendMessage("_f".getBytes());
                    }
                }

                if (s.charAt(0) == 'R') {                                 // take who is your rival 
                    char firstDigit = s.charAt(2);
                    char secondDigit = s.charAt(3);
                    int receivedInt;
                    if ((s.charAt(2)) == '0') {
                        receivedInt = (s.charAt(3) - 48);
                        System.out.println("received int " + receivedInt);
                    } else {
                        receivedInt = (((s.charAt(2) - 48) * 10) + (s.charAt(3) - 48));
                        System.out.println("received int " + receivedInt);
                    }
                    System.out.println(receivedInt);
                }
            }

        } catch (IOException ex) {
            this.Stop();

            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "->  ");
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
