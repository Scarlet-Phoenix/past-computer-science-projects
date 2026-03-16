package nothanks;

import nothanks.player.*;

import java.util.ArrayList;
import java.util.List;

public class NoThanksRunner {
    public static void main(String[] args)
    {
        NoThanksGame game = new NoThanksGame(4);

        // add the players you want (should always be 4)
        game.addPlayer(new RandomPlayer());
        game.addPlayer(new RandomPlayer());
        game.addPlayer(new RandomPlayer());
        game.addPlayer(new RandomPlayer());

        // pick one of the following lines:
        playOneGame(game);          // simulate one game...
        // playManyGames(game);     // or many games.
    }

    private static void playOneGame(NoThanksGame game)
    {
        game.DEBUG = true;  // turn on printing.
        game.playGame();
        System.out.println("Winner is player " + game.getWinner() + " (" + game.getWinnerName() + ")");
    }

    private static void playManyGames(NoThanksGame game)
    {
        game.DEBUG = false; // turn off printing.
        int numgames = 100000;
        ArrayList<Integer> totalwins = new ArrayList<>(List.of(0,0,0,0));
        for (int x = 0; x < numgames; x++)
        {
            game.resetGame();
            game.playGame();
            System.out.println("Game " + x + " final scores: " + game.getFinalScores()
                + ", winner is " + game.getWinnerName());
            int winningPlayer = game.getWinner();
            totalwins.set(winningPlayer, totalwins.get(winningPlayer) + 1);
        }
        for (int p = 0; p < 4; p++)
        {
            System.out.println("Total wins for player " + p
                    + " (" + game.getPlayerName(p) + "): " + totalwins.get(p));
        }
    }

}
