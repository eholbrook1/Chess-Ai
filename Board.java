import java.util.ArrayList;
public class Board {
  public static double inf = Double.POSITIVE_INFINITY;
  
  public static String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
  public static String[] ranks = {"1", "2", "3", "4", "5", "6", "7", "8"};
  
  private Piece[] board = new Piece[32];
  private int turn = 1; // 0=black, 1=white
  
  public Board() {
    // pawns
    for (int i = 0; i < 8; i++){
      board[i+8] = new Pawn(0, i + 60);
      board[i+24] = new Pawn(1, i + 10);
    }

    // black pieces
    board[2] = new Rook(0, 70);
    board[6] = new Knight(0, 71);
    board[4] = new Bishop(0, 72);
    board[1] = new Queen(0, 73);
    board[0] = new King(0, 74);
    board[5] = new Bishop(0, 75);
    board[7] = new Knight(0, 76);
    board[3] = new Rook(0, 77);

    // white pieces
    board[18] = new Rook(1, 0);
    board[22] = new Knight(1, 1);
    board[20] = new Bishop(1, 2);
    board[17] = new Queen(1, 3);
    board[16] = new King(1, 4);
    board[21] = new Bishop(1, 5);
    board[23] = new Knight(1, 6);
    board[19] = new Rook(1, 7);
  }

  public Board(Board other) {
    Piece[] otherBoard = other.getBoard();
    for (int i = 0; i < 32; i++)
      if (otherBoard[i] != null)
        board[i] = otherBoard[i].copy();
    turn = other.getTurn();
  }

  public static boolean onBoard(int sq) {
    return sq >= 0 && sq <= 77 && sq % 10 < 8;
  }

  public static String convSq(int sq) {
    return files[sq%10] + ranks[sq/10];
  }
  public static int convSq(String sq) {
    return (sq.charAt(1) - '1') * 10 + (sq.charAt(0) - 'a');
  }

  public static int pad(int sq) { return (sq&7) + 10 * (sq>>3); }
  public static int unpad(int sq) { return (sq%10) + 8 * (sq/10); }

  public Piece[] getBoard() { return board; }
  public int getTurn() { return turn; }

  public Piece getPiece(int sq) {
    if (!onBoard(sq)) return null;
    for (Piece p : board) if (p != null && p.getSq() == sq) return p;
    return null;
  }
  public Piece getPiece(int sq, int color) {
    if (!onBoard(sq)) return null;
    for (int i = color*16; i < (color+1)*16; i++){
      Piece p = board[i];
      if (p != null && p.getSq() == sq) return p;
    }
    return null;
  }

  public Piece getKing(int color) {
    for (int i = color*16; i < (color+1)*16; i++)
      if (board[i] != null && board[i].getSymbol().equals("K"))
        return board[i];
    return null;
  }

  public boolean hasPiece(int sq) { return getPiece(sq) != null; }
  public boolean hasPiece(int sq, int color) {
    return getPiece(sq, color) != null;
  }

  public boolean makeMove(String move) {
    int start = convSq(move.substring(0, 2));
    int end = convSq(move.substring(2));
    Piece p = getPiece(start);
    
    if (p == null) return false;
    if (p.getColor() != turn) return false;
    if (!p.isValidMove(end, this)) return false;

    // castling
    if (p.getSymbol().equals("K") && Math.abs(end - start) == 2){
      int side = (end%10) / 7;
      int rook = end + 3*side-2;
      Piece rookPiece = getPiece(rook);
      rookPiece.setSquare(end + 1-2*side);
    }

    // capturing
    Piece cap = getPiece(end);
    for (int i = 0; i < 32 && cap != null; i++)
      if (board[i] == cap) board[i] = null;
    p.setSquare(end);
    turn = 1 - turn;
    return true;
  }
  public boolean makeMove(Move move) {
    return makeMove(move.toString());
  }

  public long getValidMoves() {
    long moves = 0;
    for (int i = turn*16; i < (turn+1)*16; i++){
      if (board[i] != null)
        moves |= board[i].getValidMoves(this);
    }
    return moves;
  }

  public ArrayList<Move> getLegalMoves() {
    ArrayList<Move> moves = new ArrayList<Move>();
    for (int i = turn*16; i < (turn+1)*16; i++) {
      Piece p = board[i];
      if (p == null) continue;
      for (int sq : p.getLegalMoves(this))
        moves.add(new Move(this, p, sq));
    }
    return moves;
  }

  public double eval() {
    int score = 0;
    for (int i = 0; i < 16; i++){
      Piece p = board[i];
      if (p == null) continue;
      score -= p.value();
      score -= p.getTable()[p.getSq()];
    }
    for (int i = 16; i < 32; i++){
      Piece p = board[i];
      if (p == null) continue;
      score += p.value();

      String sq = p.getSquare();
      int index = ('8' - sq.charAt(1)) * 10 + (sq.charAt(0) - 'a');
      score += p.getTable()[index];
    }
    return score;
  }

  public double eval(int depth) { return eval(depth, -inf, inf); }
  public double eval(int depth, double alpha, double beta) {
    if (depth == 0) return eval();
    ArrayList<Move> moves = getLegalMoves();
    if (moves.size() == 0)
      return Math.pow(-1, turn) * inf;
    
    if (turn == 1){
      double value = -inf;
      for (Move m : moves){
        value = Math.max(value, m.newBoard().eval(depth - 1, alpha, beta));
        if (value > beta) break;
        alpha = Math.max(alpha, value);
      }
      return value;
    } else {
      double value = inf;
      for (Move m : moves){
        value = Math.min(value, m.newBoard().eval(depth - 1, alpha, beta));
        if (value < alpha) break;
        beta = Math.min(beta, value);
      }
      return value;
    }
  }

  public String toString() {
    String s = "-----------------";
    for (int i = 7; i >= 0; i--) {
      s += "\n|";
      for (int j = 0; j < 8; j++) {
        int sq = i * 10 + j;
        Piece p = getPiece(sq);
        if (p == null) s += " |";
        else s += p.toString() + "|";
      }
      s += "\n-----------------";
    }
    return s;
  }
}