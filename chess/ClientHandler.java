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

class ClientHandler extends Thread {
  private Socket clientSock;
  private String cliAddr;
  private ChessServer server;

  public ClientHandler(Socket paramSocket, String paramString, ChessServer paramChessServer){
    this.clientSock = paramSocket;
    this.cliAddr = paramString;
    System.out.println("Client connection from " + paramString);
    this.server = paramChessServer;
  }

  @Override
  public void run() {
    try {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(this.clientSock.getInputStream()));
      PrintWriter localPrintWriter = new PrintWriter(this.clientSock.getOutputStream(), true);
      processClient(localBufferedReader, localPrintWriter);
      this.clientSock.close();
      System.out.println("Client (" + this.cliAddr + ") connection closed\n");
      this.server.removeClient(this.clientSock);
    }
    catch (IOException localException) {
      System.err.println(localException);
    }
  }

  private void processClient(BufferedReader paramBufferedReader, PrintWriter paramPrintWriter) {
    int i = 0;
    try {
      while(i == 0) {
        String str;
        if((str = paramBufferedReader.readLine()) == null) {
            i = 1;
        } else {
          System.out.println("Client msg: " + str);
          this.server.broadcastLine(this.clientSock, str);
        }
      }
    }
    catch(IOException localIOException) {
      System.err.println(localIOException);
    }
  }
}