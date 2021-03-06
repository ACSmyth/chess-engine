package piece;

import java.util.ArrayList;
import java.util.List;
import util.DynamicChessPieceUtils;

public class Rook extends AbstractChessPiece implements ChessPiece {
  private boolean hasMoved;

  public Rook(int r, int c, boolean isWhitePiece) {
    this(r, c, isWhitePiece, false);
  }

  public Rook(int r, int c, boolean isWhitePiece, boolean hasMoved) {
    super(r, c, isWhitePiece);
    this.hasMoved = hasMoved;
  }

  @Override
  protected boolean isLegalMoveIgnoringChecks(int toR, int toC, ChessPiece[][] board) {
    return getLegalMoves(board).contains(new Move(r, c, toR, toC));
  }

  @Override
  protected List<Move> calculateLegalMovesIgnoringChecks(ChessPiece[][] board) {
    List<Move> legalMovesIgnoringChecks = new ArrayList<>();
    addMoves(board, legalMovesIgnoringChecks, false);
    return legalMovesIgnoringChecks;
  }

  private void addMoves(ChessPiece[][] board, List<Move> moves,
                              boolean includeAttackMoves) {
    DynamicChessPieceUtils.addMoves(side(), r, c, 1, 0, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, -1, 0, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 0, 1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 0, -1, board, moves, includeAttackMoves);
  }

  @Override
  protected List<Move> calculateAttackMoves(ChessPiece[][] board) {
    List<Move> attackMoves = new ArrayList<>();
    addMoves(board, attackMoves, true);
    return attackMoves;
  }


  @Override
  public ChessPiece create(int r, int c, boolean isWhitePiece) {
    return new Rook(r, c, isWhitePiece, hasMoved);
  }

  @Override
  public String display() {
    return "R";
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(hasMoved ? 10 : -10);
  }

  public boolean hasMoved() {
    return hasMoved;
  }

  @Override
  public void updatePieceMoved(int toR, int toC) {
    hasMoved = true;
    super.updatePieceMoved(toR, toC);
  }
}
