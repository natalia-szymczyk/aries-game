/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.newplayer;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.moves.MoveMove;

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
        List<MoveMove> moveMoves = moves.stream().map(move -> (MoveMove) move).collect(Collectors.toList());

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(String.format("./logs/log%d.txt", numberOfMove++), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        writer.println(moves.size());

        Object[] move = AlphaBeta(b, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, Type.MAX, writer);

//        writer.println("wybrany ruch: " + move[1].toString() + " ocena: " + move[0].toString());
        writer.close();

        return (Move) move[1];
    }

    private Object[] AlphaBeta(Board board, int depth, int alpha, int beta, Type type, PrintWriter writer){
//        TODO Ograniczenie na czas (system.get time => itp)
//        TODO Lepsza ocena heurystyczna (ocena kazdego pola odleglosc od pukntu koncowego )
//        TODO karac za bliskosc baranka przy celu! zeby zbijac baranki przy jego celu
//        TODO jak on jest 5,5 a cel 7,7 to dodaje 25

        Color color;

        if(type == Type.MAX)
            color = this.getColor();
        else
            color = getOpponent(this.getColor());

        List<Move> moves = board.getMovesFor(color);

        if(depth == 0 || moves.isEmpty()){
            return new Object[] {calculateHeuristicRate(board, writer), null};
        }

        Color winningColor = board.getWinner(getOpponent(color));

        if(winningColor == this.getColor()){
            return new Object[] {Integer.MAX_VALUE, null};
        }
        else if(winningColor == getOpponent(this.getColor())){
            return new Object[] {Integer.MIN_VALUE, null};
        }

        if(type == Type.MAX) {
            Move bestMove = null;

            for(Move childMove : moves){
                board.doMove(childMove);
                Object[] result = AlphaBeta(board, depth - 1, alpha, beta, Type.MIN, writer);
                int val = (int) result[0];
                board.undoMove(childMove);

                if(val > alpha){
                    alpha = val;
                    bestMove = childMove;
                }

                if (alpha >= beta){
                    assert bestMove != null;
                    return new Object[] {beta, bestMove};
                }
            }

            assert bestMove != null;
            return new Object[] {alpha, bestMove};
        }
        else{
            Move bestMove = null;

            for(Move childMove : moves){
                board.doMove(childMove);
                Object[] result = AlphaBeta(board, depth - 1, alpha, beta, Type.MAX, writer);
                int val = (int) result[0];
                board.undoMove(childMove);

                if(val < beta){
                    beta = val;
                    bestMove = childMove;
                }

                if (alpha >= beta){
                    assert bestMove != null;
                    return new Object[] {alpha, bestMove};
                }
            }

            assert bestMove != null;
            return new Object[] {beta, bestMove};
        }
    }

    public int calculateHeuristicRate(Board board, PrintWriter writer) {
        writer.println("fields: " + calculateFields(board, writer));
        writer.println("diff: " + calculateDifference(board));
        writer.println("rate: " + (100 * calculateDifference(board) - calculateFields(board, writer)));
        return 100 * calculateDifference(board) - calculateFields(board, writer);
    }

    public int calculateFields(Board board, PrintWriter writer) {
        int sum = 0;
        int distance;
        Field myStartingField = calculateStartingField(board, getColor());
        Field tmpField;
        writer.println("starting: " + myStartingField.x + " " + myStartingField.y);

        for(int i = 0; i < board.getSize(); i++){
            for(int j = 0; j < board.getSize(); j++){
                if(board.getState(i, j) == getColor()){
                    tmpField = new Field(i, j);
                    distance = calculateDistanceBetweenFields(tmpField, myStartingField);
                    sum += distance;
                    writer.println("MOJ: pole: " + i + " " + j + " distance: " + calculateDistanceBetweenFields(tmpField, myStartingField));
                }
                else if(board.getState(i, j) == getOpponent(this.getColor())){
                    tmpField = new Field(i, j);
                    distance = calculateDistanceBetweenFields(tmpField, myStartingField);
                    sum -= distance;
                    writer.println("JEGO: pole: " + i + " " + j + " distance: " + calculateDistanceBetweenFields(tmpField, myStartingField));
                }
            }
        }

        return sum;
    }

    public Field calculateStartingField(Board board, Color color){
        if (color == Color.PLAYER1){
            return new Field(0, 0);
        }
        else{
            return new Field(board.getSize() - 1, board.getSize() - 1);
        }
    }

    public Field calculateTargetField(Board board, Color color){
        if (color == Color.PLAYER2){
            return new Field(0, 0);
        }
        else{
            return new Field(board.getSize() - 1, board.getSize() - 1);
        }
    }

    public int calculateDistanceBetweenFields(Field field, Field target){
        return Math.abs(field.x - target.x) * Math.abs(field.y - target.y);
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

class Field {
    public int x;
    public int y;
    public Player.Color color;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
