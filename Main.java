import java.util.*;
import java.io.*;

public class Main {
  public static String standardNotation(String move, Board b) {
    int start = Board.convSq(move.substring(0, 2));
    int end = Board.convSq(move.substring(2));
    String sym = b.getPiece(start).getSymbol();
    String p = "";
    if (sym.equals("N")){
      p = "N";
      int[] dir = new int[] {-21, -19, -12, -8, 8, 12, 19, 21};
      for (int d : dir){
        int i = end + d;
        Piece p2 = b.getPiece(i);
        if (p2 != null && p2.getSymbol().equals("N")){
          if (i == start) continue;
          if (start/10 == i/10) p += (char) ('1' + (start%10));
          else p += (char) ('a' + (start/10));
          break;
        }
      }
    }
    else if (!sym.equals("P")) p = sym;
    else if (move.charAt(0) != move.charAt(2)) p += move.charAt(0);
    return p + move.substring(2);
  }

  public static Move calcBest(Board b) {
    ArrayList<Move> moves = b.getLegalMoves();
    Move bestMove = null;
    double bestEval = Double.POSITIVE_INFINITY;
    for (Move m : moves){
      double eval = m.newBoard().eval(3);
      if (eval < bestEval){
        bestEval = eval;
        bestMove = m;
      }
    }
    return bestMove;
  }
  
  public static void main(String[] args) {
    Board b = new Board();
    Piece.addTables();
    Scanner scan = new Scanner(System.in);
    System.out.println(b);
    
    ArrayList<String> openings = new ArrayList<String>();
    try {
      File openingsFile = new File("src/main/java/openings.txt");
      Scanner reader = new Scanner(openingsFile);
      while (reader.hasNextLine())
        openings.add(reader.nextLine());
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("No openings loaded");
    }
    
    String move = "", hist = "";
    while (!move.equals("quit")){
      move = scan.nextLine();
      hist += standardNotation(move, b) + " ";
      b.makeMove(move);
      
      Move bestMove;
      if (hist.replaceAll("\\w", "").length() < 8){
        int len = hist.length();
        HashMap<String,Integer> moves = new HashMap<String,Integer>();
        for (int i = 0; i < openings.size(); i++){
          String opening = openings.get(i).replace("x", "");
          if (!opening.startsWith(hist)){
            openings.remove(i--);
            continue;
          }
          String nextMove = opening.substring(len, opening.indexOf(" ", len));
          Integer count = moves.get(nextMove);
          moves.put(nextMove, count == null ? 1 : count + 1);
        }
        
        String[] keys = moves.keySet().toArray(new String[0]);
        if (moves.size() == 0) bestMove = calcBest(b);
        else if (moves.size() == 1) bestMove = new Move(b, keys[0]);
        else {
          int[] cumulativeSum = new int[moves.size()];
          cumulativeSum[0] = moves.get(keys[0]);
          for (int i = 1; i < moves.size(); i++)
            cumulativeSum[i] = cumulativeSum[i-1] + moves.get(keys[i]);
          int rand = (int) (Math.random() * cumulativeSum[keys.length-1]);
          int index = 0;
          while (index < moves.size() && rand >= cumulativeSum[index]) index++;
          bestMove = new Move(b, keys[index]);
        }
      } else bestMove = calcBest(b);
      System.out.println(bestMove);
      // printAttacks(b);
      hist += standardNotation(bestMove.toString(), b) + " ";
      b.makeMove(bestMove);
      System.out.println(b);
      // System.out.println(b.eval(5));
    }
    scan.close();
  }

  public static void printAttacks(Board b) {
    long attacks = b.getValidMoves();
    String s = "-----------------";
    for (int i = 7; i >= 0; i--) {
      s += "\n|";
      for (int j = 0; j < 8; j++) {
        int sq = i * 8 + j;
        if ((attacks & (1L << sq)) == 0) s += " |";
        else s += "A|";
      }
      s += "\n-----------------";
    }
    System.out.println(s);
  }
}