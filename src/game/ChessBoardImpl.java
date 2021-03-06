package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import piece.Bishop;
import piece.ChessPiece;
import piece.King;
import piece.Knight;
import piece.Move;
import piece.NullMove;
import piece.Pawn;
import piece.Queen;
import piece.Rook;
import util.HashingUtils;
import util.Pos;

public class ChessBoardImpl implements ChessBoard {
  private final ChessPiece[][] board;
  private Pos whiteKingPos;
  private Pos blackKingPos;
  private Map<ChessBoard, Integer> prevBoardStates;
  private Integer cachedHashCode;

  public ChessBoardImpl() {
    board = new ChessPiece[8][8];
    initBoard(0, 1, false);
    initBoard(7, -1, true);
    whiteKingPos = findKingPos(true);
    blackKingPos = findKingPos(false);
    prevBoardStates = new HashMap<>();
    cachedHashCode = null;
  }

  public ChessBoardImpl(ChessBoard board) {
    ChessBoardImpl otherBoard = (ChessBoardImpl)board;
    this.board = deepClone(otherBoard.getBoard());
    whiteKingPos = findKingPos(true);
    blackKingPos = findKingPos(false);
    prevBoardStates = new HashMap<>(((ChessBoardImpl)board).prevBoardStates);
    cachedHashCode = ((ChessBoardImpl) board).cachedHashCode;
  }


  public ChessBoardImpl(ChessPiece[][] board) {
    this.board = deepClone(board);
    whiteKingPos = findKingPos(true);
    blackKingPos = findKingPos(false);
    prevBoardStates = new HashMap<>();
    // TODO - dont have previous board states - maybe work anyway? or change everything to use ChessBoards
    cachedHashCode = null;
  }

  private ChessPiece[][] deepClone(ChessPiece[][] arr) {
    ChessPiece[][] newArr = new ChessPiece[arr.length][arr[0].length];
    for (int i = 0; i < arr.length; i++) {
      for (int p = 0; p < arr[0].length; p++) {
        if (arr[i][p] != null) {
          newArr[i][p] = arr[i][p].copy();
        }
      }
    }
    return newArr;
  }

  private void initBoard(int r, int delta, boolean isWhitePiece) {
    board[r][0] = new Rook(r, 0, isWhitePiece);
    board[r][1] = new Knight(r, 1, isWhitePiece);
    board[r][2] = new Bishop(r, 2, isWhitePiece);
    board[r][3] = new Queen(r, 3, isWhitePiece);
    board[r][4] = new King(r, 4, isWhitePiece);
    board[r][5] = new Bishop(r, 5, isWhitePiece);
    board[r][6] = new Knight(r, 6, isWhitePiece);
    board[r][7] = new Rook(r, 7, isWhitePiece);
    for (int p = 0; p < 8; p++) {
      board[r + delta][p] = new Pawn(r + delta, p, isWhitePiece);
    }
  }

  @Override
  public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol, boolean turn) {
    // from and to must be valid positions
    // must be a piece in from, and piece in from must be same color as turn
    // must either be no piece in to, or piece of opposite color
    if (!inBounds(fromRow, fromCol) || !inBounds(toRow, toCol)
            || !isOccupied(fromRow, fromCol) || board[fromRow][fromCol].side() != turn
            || (isOccupied(toRow, toCol) && board[toRow][toCol].side() == turn)) {
      return false;
    }
    // now, delegate to the piece to make sure its a valid move format of the piece
    return board[fromRow][fromCol].isLegalMove(toRow, toCol, board);
  }

  private boolean isOccupied(int fromRow, int fromCol) {
    return board[fromRow][fromCol] != null;
  }

  private boolean inBounds(int r, int c) {
    return r >= 0 && r < 8 && c >= 0 && c < 8;
  }

  @Override
  public boolean kingIsInCheck(boolean side) {
    Pos kingPos = side ? whiteKingPos : blackKingPos;
    for (int r = 0; r < 8; r++) {
      for (int c = 0; c < 8; c++) {
        if (board[r][c] != null && side != board[r][c].side()) {
          List<Move> attackMoves = board[r][c].getAttackMoves(board);
          for (Move move : attackMoves) {
            if (kingPos == null || (kingPos.r == move.toR && kingPos.c == move.toC)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private Pos findKingPos(boolean side) {
    for (int r = 0; r < 8; r++) {
      for (int c = 0; c < 8; c++) {
        if (board[r][c] != null && board[r][c] instanceof King && board[r][c].side() == side) {
          return new Pos(r, c);
        }
      }
    }
    return null;
  }

  @Override
  public void makeMove(Move move) {
    makeMove(move, false);
  }

  @Override
  public void makeMove(Move move, boolean editMode) {
    move.execute(board, editMode);
    updateBoard(move);
    /*if (!editMode) {
      prevBoardStates.put(this, prevBoardStates.getOrDefault(this, 0) + 1);
    }*/
    cachedHashCode = null;
  }

  private void updateBoard(Move move) {
    if (move instanceof NullMove) return;
    board[move.toR][move.toC].updatePieceMoved(move.toR, move.toC);
    updateKingPos(move);
    for (int r = 0; r < 8; r++) {
      for (int c = 0; c < 8; c++) {
        if (board[r][c] != null && (r != move.toR || c != move.toC)) {
          board[r][c].updatePieceNotMoved(r, c);
        }
      }
    }
  }

  private void updateKingPos(Move move) {
    if (board[move.toR][move.toC] instanceof King) {
      if (board[move.toR][move.toC].side()) {
        whiteKingPos = new Pos(move.toR, move.toC);
      } else {
        blackKingPos = new Pos(move.toR, move.toC);
      }
    }
  }

  @Override
  public void display() {
    for (int r = 0; r < 8; r++) {
      System.out.print(r + "    ");
      for (int c = 0; c < 8; c++) {
        if (board[r][c] == null) {
          System.out.print("\u001B[37m" + "☐" + "\u001B[0m");
        } else {
          System.out.print((board[r][c].side() ? "\u001B[34m" : "\u001B[31m")
                  + board[r][c].display() + "\u001B[0m");
        }
        if (c < 7) {
          System.out.print("  ");
        }
      }
      if (r < 7) {
        System.out.print("\n");
      }
    }
    System.out.print("\n\n     ");
    for (int c = 0; c < 8; c++) {
      System.out.print(c);
      if (c < 7) {
        System.out.print("  ");
      }
    }
  }

  @Override
  public List<Move> getLegalMoves(boolean side) {
    List<Move> legalMoves = new ArrayList<>();
    for (int r = 0; r < 8; r++) {
      for (int c = 0; c < 8; c++) {
        if (board[r][c] != null && board[r][c].side() == side) {
          legalMoves.addAll(board[r][c].getLegalMoves(board));
        }
      }
    }
    return legalMoves;
  }

  @Override
  public List<Move> getAttackMoves(boolean side) {
    List<Move> attackMoves = new ArrayList<>();
    for (int r = 0; r < 8; r++) {
      for (int c = 0; c < 8; c++) {
        if (board[r][c] != null && board[r][c].side() == side) {
          attackMoves.addAll(board[r][c].getAttackMoves(board));
        }
      }
    }
    return attackMoves;
  }

  @Override
  public ChessPiece[][] getBoard() {
    return board;
  }

  @Override
  public List<Move> getCaptureMoves(boolean side) {
    List<Move> legalMoves = getLegalMoves(side);
    List<Move> captureMoves = new ArrayList<>();
    for (Move m : legalMoves) {
      if (m.isCaptureMove(this)) {
        captureMoves.add(m);
      }
    }
    return captureMoves;
  }

  @Override
  public Pos getKingPos(boolean side) {
    return side ? whiteKingPos : blackKingPos;
  }

  @Override
  public String result() {
    if (kingIsInCheck(false) && getLegalMoves(false).isEmpty()) {
      return "1-0";
    } else if (kingIsInCheck(true) && getLegalMoves(true).isEmpty()) {
      return "0-1";
    } else if ((!kingIsInCheck(false) && getLegalMoves(false).isEmpty())
            || (!kingIsInCheck(true) && getLegalMoves(true).isEmpty())) {
      return "1/2-1/2";
    } else {
      return "*";
    }
  }

  @Override
  public int numBoardStateRepeats() {
    return prevBoardStates.getOrDefault(this, 1);
  }

  @Override
  public int hashCode() {
    if (cachedHashCode != null) return cachedHashCode;
    int hashCode = 0;
    for (int r=0;r<8;r++) {
      for (int c=0;c<8;c++) {
        if (board[r][c] == null) continue;
        int type;
        ChessPiece piece = board[r][c];
        if (piece instanceof Pawn && ((Pawn)piece).justAdvancedTwoSquares()) { // can be captured en passant
          type = 0;
        } else if (piece instanceof Pawn) { // can't be captured en passant
          type = 1;
        } else if (piece instanceof Knight) {
          type = 2;
        } else if (piece instanceof Bishop) {
          type = 3;
        } else if (piece instanceof Rook && !((Rook)piece).hasMoved()) { // can castle
          type = 4;
        } else if (piece instanceof Rook) { // can't castle
          type = 5;
        } else if (piece instanceof Queen) {
          type = 6;
        } else if (piece instanceof King && !((King)piece).hasMoved()) { // can castle
          type = 7;
        } else if (piece instanceof King) { // can't castle
          type = 8;
        } else {
          throw new RuntimeException();
        }
        if (!piece.side()) {
          type += 9; // white pieces are 0-8, black pieces are 9-17
        }
        hashCode ^= HashingUtils.table[type][r * 8 + c];
      }
    }
    cachedHashCode = hashCode;
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChessBoardImpl)) return false;
    return this.hashCode() == o.hashCode();
    /*
    if (this == o) return true;
    if (!(o instanceof ChessBoardImpl)) return false;
    ChessPiece[][] other = ((ChessBoardImpl)o).getBoard();
    for (int r=0;r<8;r++) {
      for (int c=0;c<8;c++) {
        if ((board[r][c] == null && board[r][c] != null)
                || (board[r][c] != null && other[r][c] == null)) {
          return false;
        } else if (board[r][c] != null) {
          // neither are null
          if (!board[r][c].getClass().equals(other[r][c].getClass())
                  || board[r][c].hashCode() != other[r][c].hashCode()) {
            return false;
          }
        }
      }
    }
    return true;
    */
  }
}