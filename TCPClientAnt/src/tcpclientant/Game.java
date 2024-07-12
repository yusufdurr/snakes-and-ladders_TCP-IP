package tcpclientant;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.mycompany.tcpclient;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author yusuf.dur
// */
//public class Game extends Thread {
//
//    Client player1;
//    Client player2;
//    boolean isOver;
//    boolean isTurn;
//
//    public Game(Client player1, Client player2) throws IOException {
//        this.player1 = player1;
//        this.player2 = player2;
//        this.isOver = false;
//        this.isTurn = false;
//
//        // starting things
//        player1.score = 1;
//        player2.score = 1;
//        player1.game = this;
//        player2.game = this;
//        player1.rival = player2;
//        player2.rival = player1;
//        player1.isInGame = true;
//        player2.isInGame = true;
//        player1.SendMessage("_getIntoGame".getBytes());
//        player2.SendMessage("_getIntoGame".getBytes());
//    }
//
//    public void handleRequest() throws IOException {
//
//        while (true) {
//
//            //is game over 
//            if (!player1.isInGame || !player2.isInGame) {
//                isOver = true;
//                break;
//            }
//            
//            // is game over in normal way
//            if(player1.score == 100 || player2.score == 100){
//                isOver = true;
//                break;
//            }
//
//            if (isTurn == false) {
//                player1.SendMessage("_go".getBytes());
//                player2.SendMessage("_stop".getBytes());
//            } else {
//                player1.SendMessage("_stop".getBytes());
//                player2.SendMessage("_go".getBytes());
//                isTurn = false;
//            }
//            
//            
//
//        }
//
//    }
//
//    @Override
//    public void run() {
//        try {
//            handleRequest();
//        } catch (IOException ex) {
//            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
