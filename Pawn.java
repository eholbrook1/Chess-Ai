public class Pawn extends Piece {
  private boolean moved2 = false;

  public Pawn(int color, int sq) {
    super(color, sq, new int[] {9, 10, 11, 20});
    symbol = "P";
    val = 100;
    if (color == 0)
      for (int i = 0; i < 4; i++) dir[i] *= -1;
  }

  public Pawn(int color, int sq, boolean moved) {
    this(color, sq);
    hasMoved = moved;
  }

  public boolean isValidMove(int newSq, Board board) {
    if (!super.isValidMove(newSq, board)) return false;
    
    int direction = 2*color - 1;
    int delta = newSq - square;
    int index = -1;
    for (int i = 0; i < 4; i++)
      if (delta == dir[i]) index = i;
    if (index == -1) return false;

    // captures
    if ((index & 1) == 0) return board.hasPiece(newSq, 1 - color);

    // moves
    delta = Math.abs(delta);
    if (board.hasPiece(newSq, 1 - color)) return false;
    if (delta == 20 && !hasMoved)
      return !board.hasPiece(square + 10*direction);
    return delta == 10;
  }

  public Pawn copy() { return new Pawn(color, square, hasMoved); }
}