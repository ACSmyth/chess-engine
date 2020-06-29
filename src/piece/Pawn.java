package piece;

public class Pawn extends AbstractChessPiece implements ChessPiece {
  private boolean hasMoved;
  private boolean justAdvancedTwoSquares;

  public Pawn(int r, int c, boolean isWhitePiece) {
    super(r, c, isWhitePiece);
    hasMoved = false;
    justAdvancedTwoSquares = false;
  }

  @Override
  protected boolean isLegalMoveIgnoringChecks(int toRow, int toCol, ChessPiece[][] board) {
    ChessPiece toPiece = board[toRow][toCol];
    if (c == toCol) {
      // if moving forward, must be no piece in either spot
      return (r - (toRow * sideAsInt()) == 1 && toPiece == null)
              ||
              (r - (toRow * sideAsInt()) == 2 && toPiece == null
              && board[toRow + sideAsInt()][toCol] == null);
    } else {
      // opposite side piece must be there
      return (r - toRow == sideAsInt() && Math.abs(c - toCol) == 1
              && toPiece != null && side() != toPiece.side())
              ||
              (board[r][toCol] != null && side() != board[r][toCol].side()
                      && board[r][toCol] instanceof Pawn
                      && ((Pawn)board[r][toCol]).justAdvancedTwoSquares);
    }
  }

  @Override
  public ChessPiece create(int r, int c, boolean isWhitePiece) {
    return new Pawn(r, c, isWhitePiece);
  }

  @Override
  public void update(int r, int c) {
    super.update(r, c);
    hasMoved = true;
    justAdvancedTwoSquares = Math.abs(r - this.r) == 2;
    // TODO - this ^ needs to be updated to false
    //  once the next move happens
  }
}