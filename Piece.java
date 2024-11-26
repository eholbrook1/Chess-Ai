import java.util.*;

public abstract class Piece {
  // child class specific
  protected String symbol;
  protected int[] dir;
  protected int val;
  protected static HashMap<String, int[]> squareTables = new HashMap<String, int[]>();

  // add to squareTables
  public static void addTables() {
    squareTables.put("P", new int[] {
       0,  0,  0,  0,  0,  0,  0,  0, 0, 0,
      50, 50, 50, 50, 50, 50, 50, 50, 0, 0,
      10, 10, 20, 30, 30, 20, 10, 10, 0, 0,
       5,  5, 10, 25, 25, 10,  5,  5, 0, 0,
       0,  0,  0, 20, 20,  0,  0,  0, 0, 0,
       5, -5,-10,  0,  0,-10, -5,  5, 0, 0,
       5, 10, 10,-20,-20, 10, 10,  5, 0, 0,
       0,  0,  0,  0,  0,  0,  0,  0
    });
    squareTables.put("N", new int[] {
      -50,-40,-30,-30,-30,-30,-40,-50, 0, 0,
      -40,-20,  0,  0,  0,  0,-20,-40, 0, 0,
      -30,  0, 10, 15, 15, 10,  0,-30, 0, 0,
      -30,  5, 15, 20, 20, 15,  5,-30, 0, 0,
      -30,  0, 15, 20, 20, 15,  0,-30, 0, 0,
      -30,  5, 10, 15, 15, 10,  5,-30, 0, 0,
      -40,-20,  0,  5,  5,  0,-20,-40, 0, 0,
      -50,-40,-30,-30,-30,-30,-40,-50
    });
    squareTables.put("B", new int[] {
      -20,-10,-10,-10,-10,-10,-10,-20, 0, 0,
      -10,  0,  0,  0,  0,  0,  0,-10, 0, 0,
      -10,  0,  5, 10, 10,  5,  0,-10, 0, 0,
      -10,  5,  5, 10, 10,  5,  5,-10, 0, 0,
      -10,  0, 10, 10, 10, 10,  0,-10, 0, 0,
      -10, 10, 10, 10, 10, 10, 10,-10, 0, 0,
      -10,  5,  0,  0,  0,  0,  5,-10, 0, 0,
      -20,-10,-10,-10,-10,-10,-10,-20
    });
    squareTables.put("R", new int[] {
       0,  0,  0,  0,  0,  0,  0,  0, 0, 0,
       5, 10, 10, 10, 10, 10, 10,  5, 0, 0,
      -5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
      -5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
      -5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
      -5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
      -5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
       0,  0,  0,  5,  5,  0,  0,  0
    });
    squareTables.put("Q", new int[] {
      -20,-10,-10, -5, -5,-10,-10,-20, 0, 0,
      -10,  0,  0,  0,  0,  0,  0,-10, 0, 0,
      -10,  0,  5,  5,  5,  5,  0,-10, 0, 0,
       -5,  0,  5,  5,  5,  5,  0, -5, 0, 0,
        0,  0,  5,  5,  5,  5,  0, -5, 0, 0,
      -10,  5,  5,  5,  5,  5,  0,-10, 0, 0,
      -10,  0,  5,  0,  0,  0,  0,-10, 0, 0,
      -20,-10,-10, -5, -5,-10,-10,-20
    });
    squareTables.put("K", new int[] {
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -20,-30,-30,-40,-40,-30,-30,-20, 0, 0,
      -10,-20,-20,-20,-20,-20,-20,-10, 0, 0,
       20, 20,  0,  0,  0,  0, 20, 20, 0, 0,
       20, 30, 10,  0,  0, 10, 30, 20
    });
  }

  // object specific
  protected int color;
  protected int square;
  protected boolean hasMoved = false;

  public Piece(int col, int sq, int[] d) {
    color = col;
    square = sq;
    dir = d;
  }

  // accessor methods
  public int getColor() { return color; }
  public String getSymbol() { return symbol; }
  public int value() { return val; }
  public int getSq() { return square; }
  public String getSquare() { return Board.convSq(square); }

  public int[] getTable() { return squareTables.get(symbol); }

  public void setSquare(int sq) { square = sq; hasMoved = true; }
  public void setSquare(int file, int rank) {
    square = rank * 10 + file;
    hasMoved = true;
  }
  public void setSquare(String sq) {
    this.setSquare(sq.charAt(0) - 'a', sq.charAt(1) - '1');
    hasMoved = true;
  }

  public abstract Piece copy();

  public boolean isValidMove(int newSq, Board board) {
    if (board.hasPiece(newSq, color) || !Board.onBoard(newSq)) return false;
    int diff = newSq - square;
    for (int d : dir) if (diff == d) return true;
    return false;
  }

  public long getValidMoves(Board board) {
    long moves = 0;
    for (int d : dir)
      if (isValidMove(square + d, board))
        moves |= 1L << Board.unpad(square + d);
    return moves;
  }

  public boolean isLegalMove(int newSq, Board board) {
    return isLegalMove(newSq, board, false);
  }
  public boolean isLegalMove(int newSq, Board board, boolean valid) {
    boolean[] conditions = {
      Board.onBoard(newSq),
      valid || isValidMove(newSq, board)
    };
    for (boolean c : conditions) if (!c) return false;
    
    Board newBoard = new Move(board, this, newSq).newBoard();
    long attacks = newBoard.getValidMoves();
    Piece king = newBoard.getKing(color);
    if (king == null){
      //System.out.println(board);
      //return false;
    }
    long kingPos = 1L << Board.unpad(king.getSq());
    return (kingPos & attacks) == 0;
  }

  public ArrayList<Integer> getLegalMoves(Board board) {
    ArrayList<Integer> moves = new ArrayList<Integer>();
    for (int d : dir)
      if (isLegalMove(square + d, board)) moves.add(square + d);
    return moves;
  }

  public String toString() {
    if (color == 1) return symbol;
    return symbol.toLowerCase();
  }
}