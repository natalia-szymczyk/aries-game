package put.ai.games.newplayer;

import java.util.*;
import java.util.stream.Collectors;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.moves.MoveMove;

public class NewPlayer extends Player {

    private enum Type {
        MAX, MIN
    }


    private long start;


    @Override
    public String getName() {
        return "Natalia Szymczyk 145250 Jan Swiatek 145390";
    }


    @Override
    public Move nextMove(Board board) {
        start = System.currentTimeMillis();

        ratedMove bestMove = AlphaBeta(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, Type.MAX);

        return bestMove.getMove();
    }


    private ratedMove AlphaBeta(Board board, int depth, int alpha, int beta, Type type){

        Color color = (type == Type.MAX) ? this.getColor() : getOpponent(this.getColor());

        List<Move> moves = board.getMovesFor(color);
        List<MoveMove> moveMoves = moves.stream().map(move -> (MoveMove) move).collect(Collectors.toList());
        sortMoves(board, moveMoves);

        if (depth == 0 || moves.isEmpty() || timeLimit()){
            return new ratedMove(calculateHeuristicRate(board), null);
        }

        Color winner = board.getWinner(getOpponent(color));

        if (winner == this.getColor()){
            return new ratedMove(Integer.MAX_VALUE, null);
        }
        else if (winner == getOpponent(this.getColor())){
            return new ratedMove(Integer.MIN_VALUE, null);
        }

        Move bestMove = null;
        if (type == Type.MAX) {
            for (Move childMove : moveMoves){
                board.doMove(childMove);
                ratedMove result = AlphaBeta(board, depth - 1, alpha, beta, Type.MIN);
                int val = result.getValue();
                board.undoMove(childMove);

                if (val > alpha){
                    alpha = val;
                    bestMove = childMove;
                }

                if (alpha >= beta){
                    assert bestMove != null;
                    return new ratedMove(beta, bestMove);
                }
            }

            assert bestMove != null;
            return new ratedMove(alpha, bestMove);
        }
        else{
            for (Move childMove : moveMoves){
                board.doMove(childMove);
                ratedMove result = AlphaBeta(board, depth - 1, alpha, beta, Type.MAX);
                int val = result.getValue();
                board.undoMove(childMove);

                if (val < beta){
                    beta = val;
                    bestMove = childMove;
                }

                if (alpha >= beta){
                    assert bestMove != null;
                    return new ratedMove(alpha, bestMove);
                }
            }

            assert bestMove != null;
            return new ratedMove(beta, bestMove);
        }
    }


    private void sortMoves(Board board, List<MoveMove> moves){
        Field starting = calculateStartingField(board, moves.get(0).getColor());
        moves.sort(Comparator.comparingInt((MoveMove a) -> calculateDistanceToTarget(a, starting)));
    }


    private int calculateDistanceToTarget(MoveMove move, Field target){
        return Math.abs(move.getDstX() - target.getX()) + Math.abs(move.getDstY() - target.getY());
    }


    private boolean timeLimit(){
        return (System.currentTimeMillis() > getTime() + start - 200);
    }


    private int calculateHeuristicRate(Board board) {
        return 100 * calculateDifference(board) + calculateFields(board);
    }


    private int calculateFields(Board board) {
        int sum = 0;
        int size = board.getSize();
        int distance;
        Field myStartingField = calculateStartingField(board, getColor());
        Field tmpField;

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if (board.getState(i, j) != Color.EMPTY){
                    tmpField = new Field(i, j);
                    distance = calculateDistanceBetweenFields(tmpField, myStartingField);
                    sum = (board.getState(i, j) == getColor()) ? (sum + distance) : (sum - distance);
                }
            }
        }
        return sum;
    }


    private Field calculateStartingField(Board board, Color color){
        return (color == Color.PLAYER1) ? new Field(0, 0) : new Field(board.getSize() - 1, board.getSize() - 1);
    }


    private int calculateDistanceBetweenFields(Field field, Field target){
        return Math.abs(field.getX() - target.getX()) * Math.abs(field.getY() - target.getY());
    }


    private int calculateDifference(Board board){
        int size = board.getSize();
        int difference = 0;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (board.getState(i, j) == this.getColor()){
                    difference += 1;
                }
                else if (board.getState(i, j) == getOpponent(this.getColor())){
                    difference -= 1;
                }
            }
        }

        return difference;
    }

}


class ratedMove{
    private final int value;
    private final Move move;

    public ratedMove(int value, Move move) {
        this.value = value;
        this.move = move;
    }

    public int getValue() {
        return value;
    }

    public Move getMove() {
        return move;
    }
}


class Field {
    private final int x;
    private final int y;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
