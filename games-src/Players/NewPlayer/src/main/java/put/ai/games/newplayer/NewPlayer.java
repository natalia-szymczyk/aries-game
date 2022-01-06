/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.newplayer;

import java.io.*;
import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class NewPlayer extends Player {

    private Random random = new Random(0xdeadbeef);
    private int numberOfMove = 0;


    @Override
    public String getName() {
        return "Natalia Szymczyk 145250 Jan Swiatek 145390";
    }


    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());

//        PrintWriter writer = null;
//        try {
//            writer = new PrintWriter(String.format("./logs/log%d.txt", numberOfMove++), "UTF-8");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        writer.println(moves.size());

        int moveProfitability;
        int bestProfitability = 0;
        Move bestMove = moves.get(0);

        for (Move move : moves){

            moveProfitability = calculateProfitability(b, move);

            if (moveProfitability > bestProfitability){
                bestMove = move;
                bestProfitability = moveProfitability;
            }

//            writer.println(move.toString() + " profitability: " + moveProfitability);
        }

//        writer.close();

        return bestMove;
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

    public int calculateDifferenceAfterMove(Board b, Move m){
        b.doMove(m);
        int difference = calculateDifference(b);
        b.undoMove(m);
        return difference;
    }

    public int calculateProfitability(Board b, Move m){
        return calculateDifferenceAfterMove(b, m) - calculateDifference(b);
    }
}
