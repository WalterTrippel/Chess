/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chess;

import java.io.*;

/**
 *
 * @author Walter
 */

class InputHandlerThread extends Thread {
  private BufferedReader in;
  ChessServerConnection srvcon;
  boolean done;

  public InputHandlerThread(ChessServerConnection paramChessServerConnection, BufferedReader paramBufferedReader) {
    this.srvcon = paramChessServerConnection;
    this.done = false;
    this.in = paramBufferedReader;
  }

  @Override
  public void run() {
    try {
      while (!this.done) {
        String str;
        if((str = this.in.readLine()) == null){
            this.done = true;
        } else {
          this.srvcon.reply(str);
        }
        if(!this.done){
            System.out.println("Received board data from server : " + str);
        }
      }

    }
    catch (IOException localException){
      System.err.println(localException);
    }
  }
}