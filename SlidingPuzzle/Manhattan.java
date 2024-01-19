package SlidingPuzzle;

import sac.State;
import sac.StateFunction;

public class Manhattan extends StateFunction {

    @Override
    public double calculate(State state) {
        SlidingPuzzle slidingPuzzle = (SlidingPuzzle) state;
        double h = 0.0;

        for (int i = 0; i < SlidingPuzzle.getN(); i++) {
            for (int j = 0; j < SlidingPuzzle.getN(); j++) {
                byte tile = slidingPuzzle.board[i][j];
                if (tile != 0) {
                    h += manhattan(slidingPuzzle, tile, i, j);
                }
            }
        }

        return h;
    }

    public int manhattan(SlidingPuzzle slidingPuzzle, byte tile, int i, int j) {
        int n = SlidingPuzzle.getN();
        int targetRow = (tile - 1) / n;
        int targetCol = (tile - 1) % n;
        return Math.abs(targetRow - i) + Math.abs(targetCol - j);
    }
}