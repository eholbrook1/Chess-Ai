public class Knight extends Piece {
  public Knight(int color, int sq) {
    super(color, sq, new int[] {-21, -19, -12, -8, 8, 12, 19, 21});
    symbol = "N";
    val = 310;
  }
  
  public Knight copy() { return new Knight(color, square); }
}