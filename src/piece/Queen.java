package piece;

import java.util.ArrayList;
import java.util.List;

import util.DynamicChessPieceUtils;

public class Queen extends AbstractChessPiece implements ChessPiece {
  public Queen(int r, int c, boolean isWhitePiece) {
    super(r, c, isWhitePiece);
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

  private void addMoves(ChessPiece[][] board, List<Move> moves, boolean includeAttackMoves) {
    DynamicChessPieceUtils.addMoves(side(), r, c, 1, 0, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, -1, 0, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 0, 1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 0, -1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 1, 1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, 1, -1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, -1, 1, board, moves, includeAttackMoves);
    DynamicChessPieceUtils.addMoves(side(), r, c, -1, -1, board, moves, includeAttackMoves);
  }

  @Override
  protected List<Move> calculateAttackMoves(ChessPiece[][] board) {
    List<Move> attackMoves = new ArrayList<>();
    addMoves(board, attackMoves, true);
    return attackMoves;
  }

  @Override
  public ChessPiece create(int r, int c, boolean isWhitePiece) {
    return new Queen(r, c, isWhitePiece);
  }

  @Override
  public String display() {
    return "Q";
  }
}
