/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.newplayer;

import java.io.*;
import java.util.*;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class NewPlayer extends Player {

    private int numberOfMove = 0;

    public enum Type {
        MAX, MIN
    }


    @Override
    public String getName() {
        return "Natalia Szymczyk 145250 Jan Swiatek 145390";
    }


    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(String.format("./logs/log%d.txt", numberOfMove++), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(moves.size());

        Object[] move = minmax(b, 3, Type.MAX);

        writer.println("wybrany ruch: " + move[1].toString() + " ocena: " + move[0].toString());
        writer.close();

        return (Move) move[1];
    }

    private Object[] minmax(Board board, int depth, Type type){
        Color color;

        if(type == Type.MAX)
            color = this.getColor();
        else
            color = getOpponent(this.getColor());

        List<Move> moves = board.getMovesFor(color);

        if(depth == 0 || moves.isEmpty()){
            return new Object[] {calculateDifference(board), null};
        }

        Color winningColor = board.getWinner(getOpponent(color));

        if(winningColor == this.getColor()){
            return new Object[] {Integer.MAX_VALUE, null};
        }
        else if(winningColor == getOpponent(this.getColor())){
            return new Object[] {Integer.MIN_VALUE, null};
        }

        if(type == Type.MAX) {
            int bestValue = Integer.MIN_VALUE;
            Move bestMove = null;

            for(Move childMove : moves){
                board.doMove(childMove);
                Object[] result = minmax(board, depth - 1, Type.MIN);
                int val = (int) result[0];
                board.undoMove(childMove);

                if(val > bestValue){
                    bestValue = val;
                    bestMove = childMove;
                }
            }

            assert bestMove != null;
            return new Object[] {bestValue, bestMove};
        }
        else{
            int bestValue = Integer.MAX_VALUE;
            Move bestMove = null;

            for(Move childMove : moves){
                board.doMove(childMove);
                Object[] result = minmax(board, depth - 1, Type.MIN);
                int val = (int) result[0];
                board.undoMove(childMove);

                if(val < bestValue){
                    bestValue = val;
                    bestMove = childMove;
                }
            }

            assert bestMove != null;
            return new Object[] {bestValue, bestMove};
        }
    }

    public void writeAllMoves(PrintWriter writer, List<Move> moves){
        writer.println("MOVES: ");
        for (Move move : moves){
            writer.println(move.toString());
        }
        writer.println(" ");
    }

    public Move calculateBestMove(Board board, List<Move> moves){
        int moveProfitability;
        int bestProfitability = 0;
        Move bestMove = moves.get(0);

        for (Move move : moves){
            moveProfitability = calculateProfitability(board, move);

            if (moveProfitability > bestProfitability){
                bestMove = move;
                bestProfitability = moveProfitability;
            }
        }

        return bestMove;
    }

    public int calculateProfitability(Board b, Move m){
        return calculateDifferenceAfterMove(b, m) - calculateDifference(b);
    }

    public int calculateDifferenceAfterMove(Board b, Move m){
        b.doMove(m);
        int difference = calculateDifference(b);
        b.undoMove(m);
        return difference;
    }

    public int calculateDifference(Board b){
        int size = b.getSize();
        int ourPawns = 0;
        int enemiesPawns = 0;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (b.getState(i, j) == Color.EMPTY){
                }
                else if (b.getState(i, j) == this.getColor()){
                    ourPawns += 1;
                }
                else {
                    enemiesPawns += 1;
                }
            }
        }

        return ourPawns - enemiesPawns;
    }

//    public Color getOpponent() {
//        switch (this.getColor()) {
//            case PLAYER1:
//                return Color.PLAYER2;
//            case PLAYER2:
//                return Color.PLAYER1;
//            default:
//                throw new IllegalArgumentException("Color must be well defined");
//        }
//    }
}

class moveRate {
    public int rate;
    public Move move;

    public moveRate(int rate, Move move) {
        this.rate = rate;
        this.move = move;
    }
}
