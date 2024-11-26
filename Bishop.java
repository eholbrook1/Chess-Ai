public class Bishop extends SlidingPiece {
  public Bishop(int color, int sq) {
    super(color, sq, new int[] {-11, -9, 9, 11});
    symbol = "B";
    val = 330;
  }
  
  public Bishop copy() { return new Bishop(color, square); }
}