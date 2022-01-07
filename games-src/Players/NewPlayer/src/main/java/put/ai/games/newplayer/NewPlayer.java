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

        Object[] move = AlphaBeta(b, 3, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, true, writer);

        writer.println("wybrany ruch: " + move[1].toString() + " ocena: " + move[0].toString());
        writer.close();

        return (Move) move[1];
    }

    double evaluate(Board board, Move move) {
        return (board.getWinner(move.getColor()) == move.getColor() ? 100 : 0) + calculateProfitability(board, move) * 10;
    }

    Object[] AlphaBeta(Board board, int depth, double alpha, double beta, boolean maximizing, PrintWriter writer) {
        Color color;

        if (maximizing){
            color = this.getColor();
        }
        else {
            color = this.getOpponent();
        }

        List<Move> availableMoves = board.getMovesFor(color);

        double bestScore;
        double valScore;
        Object[] val;
        Move bestMove = calculateBestMove(board, availableMoves);

        // evaluate at leaf
        if (depth == 0) {
            return new Object[]{ evaluate(board, bestMove), bestMove};
        }

        if (maximizing){
            bestScore = alpha;
            for (int child = 1; child <= availableMoves.size(); child++){
                Board newBoard = board.clone();
                Move newMove = bestMove;
                newBoard.doMove(newMove);
                val = AlphaBeta(newBoard, depth - 1, alpha, beta, !maximizing, writer);
                valScore = (double) val[0];

                if (valScore > alpha) {
                    bestScore = valScore;
                    bestMove = newMove;
                }

                if (alpha >= beta){
                    return new Object[]{beta, bestMove };
                }
            }
        }
        else {
            bestScore = beta;
            for (int child = 1; child <= availableMoves.size(); child++){
                Board newBoard = board.clone();
                Move newMove = availableMoves.get(0);
                newBoard.doMove(newMove);
                val = AlphaBeta(newBoard, depth - 1, alpha, beta, maximizing, writer);
                valScore = (double) val[0];

                if (valScore < beta) {
                    bestScore = valScore;
                    bestMove = newMove;
                }

                if (alpha >= beta) {
                    return new Object[]{alpha, bestMove};
                }
            }
        }

        return new Object[]{ bestScore, bestMove};
    }

    Object[] AlphaBeta2(Board board, int depth, double alpha, double beta, boolean maximizing) {
        Color color;

        if (maximizing){
            color = this.getColor();
        }
        else {
            color = this.getOpponent();
        }

        List<Move> availableMoves = board.getMovesFor(color);

        double bestScore;
        Object[] temp;
        double tempScore;
        Move bestMove = null;

        // evaluate at leaf
        if (depth == 0) {
            return new Object[]{ evaluate(board, bestMove), availableMoves.get(0) };
        }

        bestScore = alpha;

        while (availableMoves.size() > 0) {
            Board newBoard = board.clone();
            Move newMove = availableMoves.get(0);
            newBoard.doMove(newMove);
            temp = AlphaBeta2(newBoard, depth - 1, -beta, -bestScore, !maximizing);

            tempScore = -(double) temp[0];

            if (tempScore > bestScore) {
                bestScore = tempScore;
                bestMove = newMove;
            }

            if (bestScore > beta) {
                return new Object[]{ bestScore, bestMove };
            }

            availableMoves.remove(0);
        }

        return new Object[]{ bestScore, bestMove };
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
                    ;
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

    public Color getOpponent() {
        switch (this.getColor()) {
            case PLAYER1:
                return Color.PLAYER2;
            case PLAYER2:
                return Color.PLAYER1;
            default:
                throw new IllegalArgumentException("Color must be well defined");
        }
    }
}

class moveRate {
    public int rate;
    public Move move;

    public moveRate(int rate, Move move) {
        this.rate = rate;
        this.move = move;
    }
}
