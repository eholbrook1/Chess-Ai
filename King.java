public class King extends Piece {
  private boolean hasMoved = false;
  
  public King(int color, int sq) {
    super(color, sq, new int[] {-11, -10, -9, -1, 1, 9, 10, 11});
    symbol = "K";
    val = 0;
  }
  
  public King(int color, int sq, boolean moved) {
    this(color, sq);
    hasMoved = moved;
  }

  public boolean isValidMove(int newSq, Board board) {
    // normal movement
    if (super.isValidMove(newSq, board)) return true;
    if (board.hasPiece(newSq, color)) return false;

    // castling
    int delta = newSq - square;
    if (hasMoved || Math.abs(delta) != 2) return false;
    int side = delta / 2;
    Piece rook = board.getPiece(square + side * (7-side)/2);
    if (rook == null) return false;
    boolean[] conditions = {
      !board.hasPiece(square + side, color),
      !board.hasPiece(square + side*2, color),
      side == 1 || !board.hasPiece(square + side*3, color),
      rook.getSymbol().equals("R") && !rook.hasMoved
    };
    for (boolean c : conditions) if (!c) return false;
    return true;
  }
  
  public King copy() { return new King(color, square, hasMoved); }
}