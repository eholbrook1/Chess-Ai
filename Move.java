public class Move {
  public Piece piece;
  public String square;
  public Board board;
  public String scn = "";
  
  public Move(Board b, Piece p, String sq) {
    board = b;
    piece = p;
    square = sq;
  }

  public Move(Board b, String move) {
    board = b;
    square = move.substring(move.length()-2);
    scn = move;
    
    long bit = 1L << Board.unpad(Board.convSq(square));
    String sym = move.substring(0, 1);
    if (!sym.matches("[NBRQK]")) sym = "P";
    move = move.replace(sym, "");
    char ch = '0';
    if (move.length() == 3) ch = move.charAt(0);
    for (Piece p : b.getBoard()){
      if (!p.getSymbol().equals(sym) || p.getColor() != b.getTurn()) continue;
      if (ch != '0' && !p.getSquare().contains(""+ch)) continue;
      if ((p.getValidMoves(b) & bit) != 0) piece = p;
    }
  }

  public Move(Board b, Piece p, int sq) { this(b, p, Board.convSq(sq)); }

  public Board newBoard() {
    Board newBoard = new Board(board);
    newBoard.makeMove(this);
    return newBoard;
  }

  public String toString() {
    return piece.getSquare() + square;
  }
}