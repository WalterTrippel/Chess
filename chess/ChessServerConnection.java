/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chess;

import java.io.*;
import java.net.*;

/**
 *
 * @author Walter
 */

public class ChessServerConnection{
  private static final int PORT = 8030;
  private static final String HOST = "localhost";
  private byte[] input = {(byte)192, (byte)168, (byte)1, (byte)3}; 
  InputHandlerThread inputhandler;
  ChessBoard chessboard;
  private Socket sock;
  private BufferedReader in;
  private PrintWriter out;

  public ChessServerConnection(ChessBoard paramChessBoard) {
    this.chessboard = paramChessBoard;
    try {
      this.sock = new Socket(InetAddress.getLocalHost(), PORT); 
      this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
      this.out = new PrintWriter(this.sock.getOutputStream(), true);
      this.inputhandler = new InputHandlerThread(this, this.in);
      this.inputhandler.start();
    }
    catch(IOException localException){
      System.err.println(localException);
    }
  }

  public void send(String paramString) {
    this.out.println(paramString);
  }

  public synchronized void reply(String paramString) {
    this.chessboard.receiveData(paramString);
  }
}