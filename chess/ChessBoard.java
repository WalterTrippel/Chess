/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chess;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author Walter
 */
public class ChessBoard extends JPanel
  implements ImageObserver, MouseListener, MouseMotionListener {
  BufferedImage image_buffer;
  ChessServerConnection serverconnection;
  private int x;
  private int y;
  private ChessClient chessclient;
  private int myColor;
  private boolean myTurn;
  private int grabbed_piece;
  private int from_row;
  private int from_col;
  private int to_row;
  private int to_col;
  private int[][] chess_matrix = new int[8][8];

  private String[] chessmen_files = { "wking.gif", "wqueen.gif", "wrook.gif", "wbishop.gif", "wknight.gif", "wpawn.gif",
      "bking.gif", "bqueen.gif", "brook.gif", "bbishop.gif", "bknight.gif", "bpawn.gif" };

  private ImageIcon[] chessmen_images = new ImageIcon[12];

  public ChessBoard(ChessClient paramChessClient) {
    this.chessclient = paramChessClient;
    setSize(800, 800);
    CreateChessmenImages();
    this.image_buffer = new BufferedImage(400, 400, 1);
    addMouseListener(this);
    addMouseMotionListener(this);
    setBoard();
    this.serverconnection = new ChessServerConnection(this);
    this.grabbed_piece = 12;
  }

  public void resetBoard() {
    setBoard();
    repaint();
    String str = encodeBoard();
    this.serverconnection.send(str);
    this.serverconnection.send("@RESET");
  }

  private void setBoard() {
    for(int i = 2; i < 6; i++) {
      for(int j = 0; j < 8; j++) {
        this.chess_matrix[i][j] = 12;
      }
    }
    this.chess_matrix[0][0] = 8;
    this.chess_matrix[0][1] = 10;
    this.chess_matrix[0][2] = 9;
    this.chess_matrix[0][3] = 7;
    this.chess_matrix[0][4] = 6;
    this.chess_matrix[0][5] = 9;
    this.chess_matrix[0][6] = 10;
    this.chess_matrix[0][7] = 8;

    for(int k = 0; k < 8; k++){
      this.chess_matrix[1][k] = 11;
      this.chess_matrix[6][k] = 5;
    }

    this.chess_matrix[7][0] = 2;
    this.chess_matrix[7][1] = 4;
    this.chess_matrix[7][2] = 3;
    this.chess_matrix[7][3] = 1;
    this.chess_matrix[7][4] = 0;
    this.chess_matrix[7][5] = 3;
    this.chess_matrix[7][6] = 4;
    this.chess_matrix[7][7] = 2;
  }

  @Override
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5){
    return true;
  }

  @Override
  public void paint(Graphics paramGraphics){
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    drawOffscreen();
    localGraphics2D.drawImage(this.image_buffer, 0, 0, this);
  }

  private void drawOffscreen(){
    Graphics2D localGraphics2D = this.image_buffer.createGraphics();
    renderChessBoard(localGraphics2D);
    if(this.grabbed_piece != 12) {
      localGraphics2D.drawImage(this.chessmen_images[this.grabbed_piece].getImage(), this.x - 22, this.y - 22, this);
    }
  }

  private void renderChessBoard(Graphics2D paramGraphics2D){
    int i = 0, j = 0;
    int k = 0;

    for(int m = 0; m < 8; m++) {
      i = 0;
      k = m % 2 == 0 ? 1 : 0;
      for(int n = 0; n < 8; n++) {
        if(k != 0) {
            paramGraphics2D.setColor(Color.white);
        } else {
          paramGraphics2D.setColor(Color.gray);
        }
        k = k == 0 ? 1 : 0;
        paramGraphics2D.fillRect(i, j, 50, 50);
        paintChessMan(this.chess_matrix[m][n], i, j, paramGraphics2D);
        i += 50;
      }
      j += 50;
    }
  }

  private void paintChessMan(int paramInt1, int paramInt2, int paramInt3, Graphics2D paramGraphics2D) {
    if((paramInt1 < 0) || (paramInt1 >= 12)) 
        return;
    paramGraphics2D.drawImage(this.chessmen_images[paramInt1].getImage(), paramInt2 + 2, paramInt3 + 2, this);
  }

  private void CreateChessmenImages() {
    for(int i = 0; i < this.chessmen_images.length; i++) {
      this.chessmen_images[i] = new ImageIcon(this.chessmen_files[i]);
    }
  }

  @Override
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
  }

  @Override
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
  }

  @Override
  public void mouseExited(MouseEvent paramMouseEvent)
  {
  }

  @Override
  public void mousePressed(MouseEvent paramMouseEvent) {
    this.from_row = (paramMouseEvent.getY() / 50);
    this.from_col = (paramMouseEvent.getX() / 50);

    if((this.from_row < 0) || (this.from_row > 7))
        return;
    if((this.from_col < 0) || (this.from_col > 7))
        return;

    this.grabbed_piece = this.chess_matrix[this.from_row][this.from_col];

    if((getPieceType(this.grabbed_piece) != this.myColor) || (!this.myTurn)) {
      this.grabbed_piece = 12;
      return;
    }

    this.chess_matrix[this.from_row][this.from_col] = 12;
    this.x = paramMouseEvent.getX();
    this.y = paramMouseEvent.getY();
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent paramMouseEvent){
    if(this.grabbed_piece == 12)
        return;

    this.to_row = (paramMouseEvent.getY() / 50);
    this.to_col = (paramMouseEvent.getX() / 50);

    if((this.to_row < 0) || (this.to_row > 7) || (this.to_col < 0) || (this.to_col > 7)){
      this.chess_matrix[this.from_row][this.from_col] = this.grabbed_piece;
      this.grabbed_piece = 12;
      repaint();
      return;
    }

    if(((this.from_row == this.to_row) && (this.from_col == this.to_col)) ||
            (!isLegalMove(this.grabbed_piece, this.from_row, this.from_col, this.to_row, this.to_col))){
      this.chess_matrix[this.from_row][this.from_col] = this.grabbed_piece;
      this.grabbed_piece = 12;
      repaint();
      return;
    }

    if(isLegalMove(this.grabbed_piece, this.from_row, this.from_col, this.to_row, this.to_col)) {
      this.chess_matrix[this.to_row][this.to_col] = this.grabbed_piece;
    } else {
      this.chess_matrix[this.from_row][this.from_col] = this.grabbed_piece;
    }
    this.grabbed_piece = 12;
    repaint();
    String str = encodeBoard();
    this.serverconnection.send(str);
    this.serverconnection.send("@TOKEN");
    this.myTurn = false;
  }

  @Override
  public void mouseDragged(MouseEvent paramMouseEvent){
    if (this.grabbed_piece == 12) 
        return;
    this.x = paramMouseEvent.getX();
    this.y = paramMouseEvent.getY();
    repaint();
  }

  @Override
  public void mouseMoved(MouseEvent paramMouseEvent)
  {
  }

  public int getPieceType(int paramInt){
    switch(paramInt) {
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
      return 14;
    case 6:
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
      return 13;
    }
    return 12;
  }

  boolean isLegalMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if(getPieceType(paramInt1) == getPieceType(this.chess_matrix[paramInt4][paramInt5])) {
        return false;
    }
    switch(paramInt1) {
    
    case 0:
      if((Math.abs(paramInt2 - paramInt4) == 1 || paramInt2 - paramInt4 == 0) 
              && (Math.abs(paramInt3 - paramInt5) == 1 || paramInt3 - paramInt5 == 0)){
          return true;
      }
      return false;
        
    case 1:
      if((paramInt3 == paramInt5)){
          if(paramInt2 > paramInt4){
            for(int i = paramInt2; i > paramInt4; --i){
                if(chess_matrix[i][paramInt3] != 12){
                    return false;
                }
            }
            return true;
          } else {
                for(int i = paramInt4; i > paramInt2; --i){
                    if(chess_matrix[i][paramInt3] != 12){
                        return false;
                    }
                }
                return true;
          }
      }
      if((paramInt2 == paramInt4)){
          if(paramInt3 > paramInt5){
            for(int i = paramInt3; i > paramInt5; --i){
                if(chess_matrix[paramInt2][i] != 12){
                    return false;
                }
            }
            return true;
          } else {
              for(int i = paramInt5; i > paramInt3; --i){
                  if(chess_matrix[paramInt2][i] != 12){
                      return false;
                  }
              }
              return true;
          }
      }
        if(paramInt2 - paramInt4 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt2 - paramInt4 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;      
        
    case 2:
      if((paramInt3 == paramInt5)){
          if(paramInt2 > paramInt4){
            for(int i = paramInt2; i > paramInt4; --i){
                if(chess_matrix[i][paramInt3] != 12){
                    return false;
                }
            }
            return true;
          } else {
                for(int i = paramInt4; i > paramInt2; --i){
                    if(chess_matrix[i][paramInt3] != 12){
                        return false;
                    }
                }
                return true;
          }
      }
      if((paramInt2 == paramInt4)){
          if(paramInt3 > paramInt5){
            for(int i = paramInt3; i > paramInt5; --i){
                if(chess_matrix[paramInt2][i] != 12){
                    return false;
                }
            }
            return true;
          } else {
              for(int i = paramInt5; i > paramInt3; --i){
                  if(chess_matrix[paramInt2][i] != 12){
                      return false;
                  }
              }
              return true;
          }
      }
      return false;
        
    case 3:
        //System.err.println("The LINE : " + paramInt2 + " " + paramInt3 + " " + paramInt4 + " " + paramInt5);
        
        if(paramInt2 - paramInt4 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt2 - paramInt4 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
        
    case 4:
      if(paramInt2 - paramInt4 == 1){
          if(Math.abs(paramInt3 - paramInt5) == 2){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == -1){
          if(Math.abs(paramInt3 - paramInt5) == 2){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == 2){
          if(Math.abs(paramInt3 - paramInt5) == 1){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == -2){
          if(Math.abs(paramInt3 - paramInt5) == 1){
              return true;
          }
      }
      return false;
        
    case 5:
      if((paramInt2 - paramInt4 == 1) && (paramInt3 == paramInt5) && (chess_matrix[paramInt4][paramInt5] == 12)) {
        return true;
      } 
      if((paramInt2 - paramInt4 == 2) && (paramInt2 == 6) && (paramInt3 == paramInt5) && (chess_matrix[paramInt4][paramInt5] == 12)){
          return true;
      }
      if((paramInt2 - paramInt4 == 1) && (chess_matrix[paramInt4][paramInt5] != 12) && (paramInt3 != paramInt5)){
          return true;
      }
      return false;
    
    case 6:
      if((Math.abs(paramInt2 - paramInt4) == 1 || paramInt2 - paramInt4 == 0) 
              && (Math.abs(paramInt3 - paramInt5) == 1 || paramInt3 - paramInt5 == 0)){
          return true;
      }
      return false;
        
    case 7:
      if((paramInt3 == paramInt5)){
          if(paramInt2 > paramInt4){
            for(int i = paramInt2; i > paramInt4; --i){
                if(chess_matrix[i][paramInt3] != 12){
                    return false;
                }
            }
            return true;
          } else {
                for(int i = paramInt4; i > paramInt2; --i){
                    if(chess_matrix[i - 1][paramInt3] != 12){
                        return false;
                    }
                }
                return true;
          }
      }
      if((paramInt2 == paramInt4)){
          if(paramInt3 > paramInt5){
            for(int i = paramInt3; i > paramInt5; --i){
                if(chess_matrix[paramInt2][i] != 12){
                    return false;
                }
            }
            return true;
          } else {
              for(int i = paramInt5; i > paramInt3; --i){
                  if(chess_matrix[paramInt2][i - 1] != 12){
                      return false;
                  }
              }
              return true;
          }
      }
        if(paramInt2 - paramInt4 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt2 - paramInt4 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;

    case 8:
        if((paramInt3 == paramInt5)){
          if(paramInt2 > paramInt4){
            for(int i = paramInt2; i > paramInt4; --i){
                if(chess_matrix[i][paramInt3] != 12){
                    return false;
                }
            }
            return true;
          } else {
                for(int i = paramInt4; i > paramInt2; --i){
                    if(chess_matrix[i - 1][paramInt3] != 12){
                        return false;
                    }
                }
                return true;
          }
      }
      if((paramInt2 == paramInt4)){
          if(paramInt3 > paramInt5){
            for(int i = paramInt3; i > paramInt5; --i){
                if(chess_matrix[paramInt2][i] != 12){
                    return false;
                }
            }
            return true;
          } else {
              for(int i = paramInt5; i > paramInt3; --i){
                  if(chess_matrix[paramInt2][i - 1] != 12){
                      return false;
                  }
              }
              return true;
          }
      }
      return false;
        
    case 9:        
        if(paramInt2 - paramInt4 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt2 - paramInt4 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt3 - paramInt5){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        
        if(paramInt4 - paramInt2 == paramInt5 - paramInt3){
            if(paramInt2 > paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 + 1; i < paramInt2 && j < paramInt3; ++i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 > paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 + 1, j = paramInt5 - 1; i < paramInt2 && j > paramInt3; ++i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 > paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 + 1; i > paramInt2 && j < paramInt3; --i, ++j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            } else if(paramInt2 < paramInt4 && paramInt3 < paramInt5){
                for(int i = paramInt4 - 1, j = paramInt5 - 1; i > paramInt2 && j > paramInt3; --i, --j){
                    if(chess_matrix[i][j] != 12){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
   
    case 10:
      if(paramInt2 - paramInt4 == 1){
          if(Math.abs(paramInt3 - paramInt5) == 2){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == -1){
          if(Math.abs(paramInt3 - paramInt5) == 2){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == 2){
          if(Math.abs(paramInt3 - paramInt5) == 1){
              return true;
          }
      }
      if(paramInt2 - paramInt4 == -2){
          if(Math.abs(paramInt3 - paramInt5) == 1){
              return true;
          }
      }
      return false;
        
    case 11:
        if((paramInt2 - paramInt4 == -1) && (paramInt3 == paramInt5) && (chess_matrix[paramInt4][paramInt5] == 12)) {
            return true;
        } 
        if((paramInt2 - paramInt4 == -2) && (paramInt2 == 1) && (paramInt3 == paramInt5) && (chess_matrix[paramInt4][paramInt5] == 12)){
            return true;
        } 
        if((paramInt2 - paramInt4 == -1) && (chess_matrix[paramInt4][paramInt5] != 12) && (paramInt3 != paramInt5)){
            return true;
        }
        return false;
    }
    return true;
  }

  public synchronized void receiveData(String paramString) {
    if(paramString.charAt(0) == '@') {
      processCommand(paramString);
      return;
    }
    decodeBoard(paramString);
    repaint();
  }

  private void processCommand(String paramString) {
    if(paramString.compareTo("@BLACK") == 0) {
      this.myColor = 13;
      this.chessclient.setTitle("Chess Client - BLACK");
      resetBoard();
    }
    else if(paramString.compareTo("@WHITE") == 0) {
      //System.out.println("I am WHITE");
      this.myColor = 14;
      this.chessclient.setTitle("Chess Client - WHITE");
      resetBoard();
      this.myTurn = true;
    }
    if(paramString.compareTo("@RESET") == 0) {
      if(this.myColor == 14){
          this.myTurn = true;
      }
    }
    else if(paramString.compareTo("@TOKEN") == 0) {
        this.myTurn = true;
    }
  }

  public String encodeBoard() {
    String str = "";
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        switch (this.chess_matrix[i][j]) {
        case 0:
          str = str + "A"; break;
        case 1:
          str = str + "B"; break;
        case 2:
          str = str + "C"; break;
        case 3:
          str = str + "D"; break;
        case 4:
          str = str + "E"; break;
        case 5:
          str = str + "F"; break;
        case 6:
          str = str + "G"; break;
        case 7:
          str = str + "H"; break;
        case 8:
          str = str + "I"; break;
        case 9:
          str = str + "J"; break;
        case 10:
          str = str + "K"; break;
        case 11:
          str = str + "L"; break;
        case 12:
          str = str + "M";
        }
      }
    }
    return str;
  }

  public void decodeBoard(String paramString) {
    if (paramString.length() < 64) 
        return;
    for(int m = 0; m < 64; m++){
      int i = m / 8;
      int j = m % 8;
      int k = paramString.charAt(m);

      switch (k) {
      case 65:
        this.chess_matrix[i][j] = 0; break;
      case 66:
        this.chess_matrix[i][j] = 1; break;
      case 67:
        this.chess_matrix[i][j] = 2; break;
      case 68:
        this.chess_matrix[i][j] = 3; break;
      case 69:
        this.chess_matrix[i][j] = 4; break;
      case 70:
        this.chess_matrix[i][j] = 5; break;
      case 71:
        this.chess_matrix[i][j] = 6; break;
      case 72:
        this.chess_matrix[i][j] = 7; break;
      case 73:
        this.chess_matrix[i][j] = 8; break;
      case 74:
        this.chess_matrix[i][j] = 9; break;
      case 75:
        this.chess_matrix[i][j] = 10; break;
      case 76:
        this.chess_matrix[i][j] = 11; break;
      case 77:
        this.chess_matrix[i][j] = 12;
      }
    }
  }
}