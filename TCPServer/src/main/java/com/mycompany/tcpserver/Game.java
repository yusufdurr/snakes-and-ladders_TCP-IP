/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcpserver;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf.dur
 */
public class Game extends Thread {

    Server server;
    ServerClient player1;
    ServerClient player2;
    boolean isOver;

    public Game(ServerClient player1, ServerClient player2, Server server) throws IOException {
        this.server = server;
        this.player1 = player1;
        this.player2 = player2;
        this.isOver = false;

        // starting things
        player1.score = 0;
        player2.score = 0;
        player1.game = this;
        player2.game = this;
        player1.rival = player2;
        player2.rival = player1;
        player1.isInGame = true;
        player2.isInGame = true;
        player1.isMyTurn = true;
        player2.isMyTurn = false;
        player1.SendMessage("_getIntoGame".getBytes());
        player2.SendMessage("_getIntoGame".getBytes());

        player1.rival.SendMessage(("_R_" + server.clients.indexOf(player1) + "").getBytes());       // send rival who is your rival
        player2.rival.SendMessage(("_R_" + server.clients.indexOf(player2) + "").getBytes());       // send rival who is your rival

    }

    @Override
    public void run() {
        try {
            while (true) {
                //is game over in normal way or some player left the game 
                if ((!player1.isInGame || !player2.isInGame) || (player1.score >= 100 || player2.score >= 100)) {
                    isOver = true;
                    server.games.remove(this);
                    // reset players 
                    player1.score = 0;
                    player2.score = 0;
                    player1.game = null;
                    player2.game = null;
                    player1.rival = null;
                    player2.rival = null;
                    player1.isInGame = false;
                    player2.isInGame = false;
                    player1.isMyTurn = false;
                    player2.isMyTurn = false;
                    
                    
                    break;
                }
                Thread.sleep(500);
                if (player1.isMyTurn == true && player2.isMyTurn == false) {
                    player1.SendMessage("_go".getBytes());
                    player2.SendMessage("_wait".getBytes());
                }
                if (player1.isMyTurn == false && player2.isMyTurn == true) {
                    player1.SendMessage("_wait".getBytes());
                    player2.SendMessage("_go".getBytes());
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
