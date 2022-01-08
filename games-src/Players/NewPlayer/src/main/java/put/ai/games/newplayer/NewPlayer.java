package put.ai.games.newplayer;

import java.util.*;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

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

        ratedMove bestMove = AlphaBeta(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, Type.MAX);

        return bestMove.getMove();
    }


    private ratedMove AlphaBeta(Board board, int depth, int alpha, int beta, Type type){

        Color color = (type == Type.MAX) ? this.getColor() : getOpponent(this.getColor());

        List<Move> moves = board.getMovesFor(color);

        if(depth == 0 || moves.isEmpty() || timeLimit()){
            return new ratedMove(calculateHeuristicRate(board), null);
        }

        Color winner = board.getWinner(getOpponent(color));

        if(winner == this.getColor()){
            return new ratedMove(Integer.MAX_VALUE, null);
        }
        else if(winner == getOpponent(this.getColor())){
            return new ratedMove(Integer.MIN_VALUE, null);
        }

        Move bestMove = null;
        if(type == Type.MAX) {
            for(Move childMove : moves){
                board.doMove(childMove);
                ratedMove result = AlphaBeta(board, depth - 1, alpha, beta, Type.MIN);
                int val = result.getValue();
                board.undoMove(childMove);

                if(val > alpha){
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
            for(Move childMove : moves){
                board.doMove(childMove);
                ratedMove result = AlphaBeta(board, depth - 1, alpha, beta, Type.MAX);
                int val = result.getValue();
                board.undoMove(childMove);

                if(val < beta){
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


    private boolean timeLimit(){
        return (System.currentTimeMillis() > getTime() + start - 200);
    }


    private int calculateHeuristicRate(Board board) {
//        TODO popatrzec na rozne kombinacje
        return 100 * calculateDifference(board) + calculateFields(board);
    }


    private int calculateFields(Board board) {
        int sum = 0;
        int distance;
        Field myStartingField = calculateStartingField(board, getColor());
        Field tmpField;

        for(int i = 0; i < board.getSize(); i++){
            for(int j = 0; j < board.getSize(); j++){
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


    private int calculateDifference(Board b){
        int size = b.getSize();
        int difference = 0;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if(b.getState(i, j) == this.getColor()){
                    difference += 1;
                }
                else if (b.getState(i, j) == getOpponent(this.getColor())){
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
