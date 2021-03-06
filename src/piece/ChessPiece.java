package piece;

import java.util.List;

public interface ChessPiece extends Cloneable {
  boolean side();

  int sideAsInt();

  boolean isLegalMove(int toR, int toC, ChessPiece[][] board);

  ChessPiece copy();

  ChessPiece create(int r, int c, boolean isWhitePiece);

  void updatePieceMoved(int toR, int toC);

  void updatePieceNotMoved(int toR, int toC);

  List<Move> getLegalMoves(ChessPiece[][] board);

  List<Move> getAttackMoves(ChessPiece[][] board);

  String display();
}
