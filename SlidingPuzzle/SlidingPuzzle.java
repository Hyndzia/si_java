package SlidingPuzzle;

import sac.graph.*;

import java.util.*;

public class SlidingPuzzle extends GraphStateImpl {
    private static int n;
    private int emptyIndex;
    public byte[][] board;

    public SlidingPuzzle (int N) {
        SlidingPuzzle.n = N;
        board = new byte[n][n];
        byte cnt = 0;
        for (byte i = 0; i < n; i++) {
            for (byte j = 0; j < n; j++, cnt++) {
                board[i][j] = cnt;
            }
        }
        emptyIndex = 0;
    }

    public static int getN() {
        return n;
    }

    public int getEmptyIndex() {
        return emptyIndex;
    }

    public SlidingPuzzle (SlidingPuzzle parent) {
        board = new byte[n][n];
        for (byte i = 0; i < n; i++)
            for (byte j = 0; j < n; j++)
                board[i][j] = parent.board[i][j];
        emptyIndex = parent.emptyIndex;
    }

    public boolean makeMove(int move) {
        try {
            if (move == 0) { //L
                board[emptyIndex / n][emptyIndex % n] = board[emptyIndex / n][emptyIndex % n + 1];
                board[emptyIndex / n][emptyIndex % n + 1] = 0;
                emptyIndex = emptyIndex + 1;
            } else if (move == 1) { //U
                board[emptyIndex / n][emptyIndex % n] = board[emptyIndex / n - 1][emptyIndex % n];
                board[emptyIndex / n - 1][emptyIndex % n] = 0;
                emptyIndex = emptyIndex - n;
            } else if (move == 2) { //R
                board[emptyIndex / n][emptyIndex % n] = board[emptyIndex / n][emptyIndex % n - 1];
                board[emptyIndex / n][emptyIndex % n - 1] = 0;
                emptyIndex = emptyIndex - 1;
            } else { //D
                board[emptyIndex / n][emptyIndex % n] = board[emptyIndex / n + 1][emptyIndex % n];
                board[emptyIndex / n + 1][emptyIndex % n] = 0;
                emptyIndex = emptyIndex + n;
            }
        } catch (ArrayIndexOutOfBoundsException error) {
            return false;
        }
        return true;
    }

    public void shuffle(int numberOfMoves) {
        Random rand = new Random();
        for (int i = 0; i < numberOfMoves; i++) {
            List<Integer> validMoves = new ArrayList<>();
            for (int move = 0; move <= 3; move++) {
                if (makeMove(move)) {
                    validMoves.add(move);
                }
            }
            if (validMoves.isEmpty()) {
                break;
            }
            int selectedMove = validMoves.get(rand.nextInt(validMoves.size()));
            makeMove(selectedMove);
        }
    }

    @Override
    public List<GraphState> generateChildren() {
        List<GraphState> children = new ArrayList<>();
        Map<Integer, String> dir_map = new HashMap<>(){{
            put(0, "L");
            put(1, "U");
            put(2, "R");
            put(3, "D");
        }};
        for (int i =0; i <= 3; i++) {
            SlidingPuzzle child = new SlidingPuzzle(this);
            if (child.makeMove(i)) {
                String move = "";
                move = dir_map.get(i);
                child.setMoveName(move);
                children.add(child);
            }
        }
        return children;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder line = new StringBuilder();
        final int cellSize = 5;
        for (int i = 0; i < n * cellSize + 1; i++) {
            line.append("−");
        }
        stringBuilder.append(line).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int boardAtK = board[i][j];
                stringBuilder.append(String.format("|%1$3d", boardAtK));
            }
            stringBuilder.append("|\n").append(line);
            if (i < n - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean isSolution() {
        byte cnt = 0;
        for (byte i = 0; i < n; i++) {
            for (byte j = 0; j < n; j++, cnt++) {
                if (board[i][j] != cnt) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode () {
        byte[] flatBoard = new byte[n * n];
        int k = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                flatBoard[k++] = board[i][j];
        return Arrays.hashCode(flatBoard);
        //return Arrays.deepHashCode(board);
    }


    public static void main(String[] args) {
        SlidingPuzzle slidingPuzzle = new SlidingPuzzle(3);
        slidingPuzzle.shuffle(1000);
        System.out.println("SLIDING PUZZLE TO SOLVE: \n" + slidingPuzzle);
        GraphSearchConfigurator conf = new GraphSearchConfigurator();
        SlidingPuzzle.setHFunction(new Manhattan());
        GraphSearchAlgorithm algorithm = new AStar(slidingPuzzle, conf);

        algorithm.execute();
        List<GraphState> solutions = algorithm.getSolutions();
        if(algorithm.getSolutions().isEmpty()) System.out.println("puste!");

        for (GraphState solution : solutions) {
            System.out.println("SOLUTION: \n" + solution);
            System.out.println("PATH LENGTH: " + solution.getPath().size());
            System.out.println("MOVES ALONG PATH:  " + solution.getMovesAlongPath());
            System.out.println("CLOSED STATES:  " + algorithm.getClosedStatesCount());
            System.out.println("OPEN STATES:  " + algorithm.getOpenSet().size());
            System.out.println("DURATION TIME:  " + algorithm.getDurationTime() + " ms");
        }
    }
    public static void main1(String[] args) {
        int open = 0;
        int closed = 0; int time = 0; int pathLength = 0;
        for(int i = 0; i<100;i++) {
            SlidingPuzzle slidingPuzzle = new SlidingPuzzle(3);
            slidingPuzzle.shuffle(1000);
            //System.out.println("SLIDING PUZZLE TO SOLVE: \n" + slidingPuzzle);
            GraphSearchConfigurator conf = new GraphSearchConfigurator();
            SlidingPuzzle.setHFunction(new MisplacedTiles());
            GraphSearchAlgorithm algorithm = new AStar(slidingPuzzle, conf);

            algorithm.execute();

            List<GraphState> solutions = algorithm.getSolutions();
            for (GraphState solution : solutions) {
                open += algorithm.getOpenSet().size();
                closed += algorithm.getClosedStatesCount();
                time += algorithm.getDurationTime();
                pathLength += solution.getPath().size();
            }
            System.out.println("iter: " + i + "\n");
        }
        System.out.println("PATH LENGTH: " + pathLength/100);
        System.out.println("CLOSED STATES:  " + closed/100);
        System.out.println("OPEN STATES:  " + open/100);
        System.out.println("DURATION TIME:  " + time/100 + " ms");

        /*if(algorithm.getSolutions().isEmpty()) System.out.println("puste!");

        for (GraphState solution : solutions) {
            System.out.println("SOLUTION: \n" + solution);
            System.out.println("PATH LENGTH: " + solution.getPath().size());
            System.out.println("MOVES ALONG PATH:  " + solution.getMovesAlongPath());
            System.out.println("CLOSED STATES:  " + algorithm.getClosedStatesCount());
            System.out.println("OPEN STATES:  " + algorithm.getOpenSet().size());
            System.out.println("DURATION TIME:  " + algorithm.getDurationTime() + " ms");
        }*/

    }

}
