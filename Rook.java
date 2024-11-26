public class Rook extends SlidingPiece {
  public Rook(int color, int sq) {
    super(color, sq, new int[] {-10, -1, 1, 10});
    symbol = "R";
    val = 500;
  }

  public Rook(int color, int sq, boolean moved) {
    this(color, sq);
    hasMoved = moved;
  }

  public Rook copy() { return new Rook(color, square, hasMoved); }
}