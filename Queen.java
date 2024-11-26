public class Queen extends SlidingPiece {
  public Queen(int color, int sq) {
    super(color, sq, new int[] {-11, -10, -9, -1, 1, 9, 10, 11});
    symbol = "Q";
    val = 900;
  }

  public Queen copy() { return new Queen(color, square); }
}