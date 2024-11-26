import java.util.ArrayList;

public abstract class SlidingPiece extends Piece {
  public SlidingPiece(int color, int sq, int[] dir) {
    super(color, sq, dir);
  }

  public boolean isValidMove(int newSq, Board board) {
    if (board.hasPiece(newSq, color) || !Board.onBoard(newSq)) return false;
    int diff = newSq - square;
    int delta = 0;
    for (int d : dir)
      if (diff % d == 0 && diff*d > 0 && diff / d < 8) delta = d;
    if (delta == 0) return false;
    for (int s = square + delta; s != newSq; s += delta)
      if (!Board.onBoard(s) || board.getPiece(s) != null) return false;
    return true;
  }

  public long getValidMoves(Board board) {
    long moves = 0;
    for (int d : dir) {
      for (int sq = square+d; Board.onBoard(sq); sq += d){
        if (board.hasPiece(sq, color)) break;
        moves |= 1L << Board.unpad(sq);
        if (board.hasPiece(sq, 1-color)) break;
      }
    }
    return moves;
  }

  public ArrayList<Integer> getLegalMoves(Board board) {
    ArrayList<Integer> moves = new ArrayList<Integer>();
    for (int d : dir) {
      for (int sq = square+d; Board.onBoard(sq); sq += d){
        if (board.hasPiece(sq, color)) break;
        if (isLegalMove(sq, board, true)) moves.add(sq);
        if (board.hasPiece(sq, 1-color)) break;
      }
    }
    return moves;
  }
}