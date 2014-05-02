/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chess;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Walter
 */

public class ChessClient extends JFrame {
  private ChessBoard board;
  private BorderLayout layout;
  private JMenuBar bar = new JMenuBar();
  private JMenu gameMenu = new JMenu("Game");
  private JMenuItem resetItem = new JMenuItem("Reset");
  private JMenuItem exitItem = new JMenuItem("Exit");

  public ChessClient() {
    super("Chess Client");
    Container localContainer = getContentPane();

    this.layout = new BorderLayout(1, 1);
    localContainer.setLayout(this.layout);

    this.board = new ChessBoard(this);
    localContainer.add(this.board, "Center");

    setJMenuBar(this.bar);

    this.gameMenu.add(this.resetItem);
    this.gameMenu.add(this.exitItem);

    this.bar.add(this.gameMenu);

    this.exitItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent paramAnonymousActionEvent){
        System.exit(0);
      }
    });
    
    this.resetItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent paramAnonymousActionEvent){
        ChessClient.this.board.resetBoard();
      }
    });
    setResizable(false);
    setSize(400, 450);
  }

  public static void main(String[] paramArrayOfString){
    ChessClient localChessClient = new ChessClient();
    localChessClient.addWindowListener(new WindowAdapter(){
      @Override
      public void windowClosing(WindowEvent paramAnonymousWindowEvent){
        System.exit(0);
      }
    });
    
    localChessClient.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - localChessClient.getWidth())/2,
            (Toolkit.getDefaultToolkit().getScreenSize().height - localChessClient.getHeight())/2);
    localChessClient.show();
  }
}