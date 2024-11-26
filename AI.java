import java.util.*;
import java.io.*;

public class AI {
  private static final int inf = Integer.MAX_VALUE;
  
  private ArrayList<String> openings = new ArrayList<String>();
  private HashMap<String, int[]> squareTables = new HashMap<String, int[]>();

  private int maxDepth;
  private String history = "";

  public AI() { this(3); }
  public AI(int depth) {
    if (depth > 0) maxDepth = depth;

    // load openings
    try {
      File openingsFile = new File("src/main/java/openings.txt");
      Scanner reader = new Scanner(openingsFile);
      while (reader.hasNextLine())
        openings.add(reader.nextLine());
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("No openings loaded");
    }

    // add square tables
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

    // King's different tables:
    squareTables.put("K_e", new int[] { // early/middle game
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
      -20,-30,-30,-40,-40,-30,-30,-20, 0, 0,
      -10,-20,-20,-20,-20,-20,-20,-10, 0, 0,
       20, 20,  0,  0,  0,  0, 20, 20, 0, 0,
       20, 30, 10,  0,  0, 10, 30, 20
    });
    squareTables.put("K_l", new int[] { // late game
      -50,-40,-30,-20,-20,-30,-40,-50, 0, 0,
      -30,-20,-10,  0,  0,-10,-20,-30, 0, 0,
      -30,-10, 20, 30, 30, 20,-10,-30, 0, 0,
      -30,-10, 30, 40, 40, 30,-10,-30, 0, 0,
      -30,-10, 30, 40, 40, 30,-10,-30, 0, 0,
      -30,-10, 20, 30, 30, 20,-10,-30, 0, 0,
      -30,-30,  0,  0,  0,  0,-30,-30, 0, 0,
      -50,-30,-30,-30,-30,-30,-30,-50
    });
  }

  public int[] getTable(String symbol) { return squareTables.get(symbol); }

  private void inc(ArrayList<Integer> list, int i) {
    list.set(i, list.get(i)+1);
  }
  public Move calcOpenings(Board board) {
    ArrayList<String> moves = new ArrayList<String>();
    ArrayList<Integer> numTimes = new ArrayList<Integer>();
    for (int i = 0; i < openings.size(); i++){
      String opening = openings.get(i).replace("x", "");
      if (!opening.startsWith(history)){
        openings.remove(i--);
        continue;
      }
      int len = history.length();
      String nextMove = opening.substring(len, opening.indexOf(" ", len));
      if (moves.contains(nextMove)) inc(numTimes, moves.indexOf(nextMove));
      else {
        moves.add(nextMove);
        numTimes.add(1);
      }
    }
    if (openings.size() == 0) return null;

    int[] sum = new int[moves.size()];
    sum[0] = numTimes.get(0);
    for (int i = 1; i < moves.size(); i++) sum[i] = sum[i-1] + numTimes.get(i);
    int rand = (int) (Math.random() * sum[sum.length-1]);
    for (int i = 0; i < moves.size(); i++)
      if (sum[i] > rand) return new Move(board, moves.get(i));
    return null;
  }

  public Move calcMove(Board board) {
    ArrayList<Move> moves = board.getLegalMoves();
    Move bestMove = moves.get(0);
    int bestEval = inf;
    for (Move m : moves){
      int val = eval(m.newBoard());
      if (val < bestEval){
        bestEval = val;
        bestMove = m;
      }
    }
    return bestMove;
  }

  public int evalNode(Board board) {
    int score = 0;
    Piece[] b = board.getBoard();
    
    for (int i = 0; i < 16; i++){
      Piece p = b[i];
      if (p == null) continue;
      score -= p.value();
      score -= getTable(p.getSymbol())[p.getSq()];
    }
    for (int i = 16; i < 32; i++){
      Piece p = b[i];
      if (p == null) continue;
      score += p.value();

      String sq = p.getSquare();
      int index = ('8' - sq.charAt(1)) * 10 + (sq.charAt(0) - 'a');
      score += getTable(p.getSymbol())[index];
    }
    return score;
  }

  public int eval(Board b) { return eval(b, maxDepth, -inf, inf); }
  public int eval(Board board, int depth, int alpha, int beta) {
    if (depth == 0) return evalNode(board);
    int turn = board.getTurn();
    ArrayList<Move> moves = board.getLegalMoves();
    if (moves.size() == 0)
      return (int) Math.pow(-1, turn) * inf;

    if (turn == 1){
      int value = -inf;
      for (Move m : moves){
        value = Math.max(value, eval(m.newBoard(), depth - 1, alpha, beta));
        if (value > beta) break;
        alpha = Math.max(alpha, value);
      }
      return value;
    } else {
      int value = inf;
      for (Move m : moves){
        value = Math.min(value, eval(m.newBoard(), depth - 1, alpha, beta));
        if (value < alpha) break;
        beta = Math.min(beta, value);
      }
      return value;
    }
  }
}