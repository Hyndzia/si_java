package SlidingPuzzle;

import sac.State;
import sac.StateFunction;

public class MisplacedTiles extends StateFunction {

    @Override
    public double calculate (State state) {
         SlidingPuzzle slidingPuzzle = (SlidingPuzzle) state;
         double h = 0.0;
         int n = SlidingPuzzle.getN();
         for (int i = 0; i < slidingPuzzle.board.length; i++) {
             if ((i != slidingPuzzle.getEmptyIndex()) && (slidingPuzzle.board[i/n][i%n] != i))
                 h += 1.0;
             }
         return h;
         }
 }

