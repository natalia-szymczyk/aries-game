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


    @Override
    public String getName() {
        return "Natalia Szymczyk Jan Swiatek";
    }


    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("the-file-name.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println("The first line");
        writer.println("The second line");
        writer.close();


        return moves.get(random.nextInt(moves.size()));
    }
}
