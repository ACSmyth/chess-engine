package bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import game.ChessBoard;
import game.ChessBoardImpl;
import piece.Move;

public class MinimaxWithABPruningBot implements Bot {
  private final Evaluator evaluator;
  private final MoveSorter moveSorter;
  public static int depth;
  private double prevEval;

  public MinimaxWithABPruningBot() {
    evaluator = new ComplexEvaluator();
    moveSorter = new SimpleMoveSorter();
    depth = 4;
    prevEval = 0;
  }

  @Override
  public Move chooseMove(ChessBoard board, boolean turn) {
    MoveEvalPair result = minimax(board, turn, depth, new HashMap<>(),
            -999999, 999999);
    Move chosenMove = result.move;
    prevEval = result.eval;
    return chosenMove;
  }

  MoveEvalPair minimax(ChessBoard board, boolean turn, int depthLeft,
                       Map<ChessBoard, MoveEvalPair> cachedEvals,
                       double alpha, double beta) {
    if (depthLeft <= 0 && board.isQuiet()) { // fine since calculating attack moves in evaluator anyway, cached one place or another
      double eval = evaluator.evaluate(board);
      MoveEvalPair pair = new MoveEvalPair(null, eval);
      return pair;
    } else if (depthLeft <= 0) {
      // not quiet
      // minimax, but only on capture moves with non-negative (include 0) value deltas
    }
    List<Move> legalMoves = board.getLegalMoves(turn);
    if (legalMoves.isEmpty()) {
      if (board.kingIsInCheck(turn)) {
        MoveEvalPair pair = new MoveEvalPair(null, turn ? -999999 : 999999);
        return pair;
      } else {
        MoveEvalPair pair = new MoveEvalPair(null, 0);
        return pair;
      }
    }
    Move bestMove = null;
    double bestEval = turn ? -999999 : 999999;
    double newAlpha = alpha;
    double newBeta = beta;

    // order legal moves
    moveSorter.sort(legalMoves, board);

    for (Move m : legalMoves) {
      ChessBoard newBoard = new ChessBoardImpl(board);
      newBoard.makeMove(m);
      double eval = minimax(newBoard, !turn, depthLeft - 1, cachedEvals, newAlpha, newBeta).eval;
      // alpha cutoff
      if (!turn && eval < alpha) {
        return new MoveEvalPair(m, eval);
      }
      // beta cutoff
      if (turn && eval > beta) {
        return new MoveEvalPair(m, eval);
      }

      bestEval = turn ? Math.max(eval, bestEval) : Math.min(eval, bestEval);
      if (eval == bestEval) {
        bestMove = m;
      }

      // update alpha and beta
      if (turn) {
        newAlpha = bestEval;
      } else {
        newBeta = bestEval;
      }
    }
    MoveEvalPair pair = new MoveEvalPair(bestMove, bestEval);
    return pair;
  }

  @Override
  public double getPrevEval() {
    return prevEval;
  }


  private static class MoveEvalPair {
    private final Move move;
    private final double eval;

    private MoveEvalPair(Move move, double eval) {
      this.move = move;
      this.eval = eval;
    }
  }
}