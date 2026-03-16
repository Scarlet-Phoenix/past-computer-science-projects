package nothanks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nothanks.player.Player;
import sortedlist.SortedList;

public class NoThanksGame {
    private final int numPlayers; // number of players in the game
    private final List<Player> players;   // list of the players (AI simulators)
    private final List<SortedList<Integer>> playerHands;    // list of the cards each player is holding
    private final List<Integer> playerChips;  // list of the # of chips each player is holding
    private final List<Integer> cardsInDeck;  // list of cards in deck
    private final List<Integer> finalScores; // list of final scores
    private GameState gameState;

    public boolean DEBUG = true;

    private enum GameState {
        NOT_ENOUGH_PLAYERS, PREGAME, PLAYING, OVER
        // pregame = at least one player, ready to play the game
    }

    public NoThanksGame(int numPlayers) {
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        this.playerHands = new ArrayList<>();
        this.playerChips = new ArrayList<>();
        this.cardsInDeck = new ArrayList<>();
        this.finalScores = new ArrayList<>();
        gameState = GameState.NOT_ENOUGH_PLAYERS;
    }

    /**
     * Add a new player to the game.
     */
    public void addPlayer(Player player) {
        if (!(gameState == GameState.NOT_ENOUGH_PLAYERS || gameState == GameState.PREGAME))
            throw new IllegalStateException();

        players.add(player);

        if (players.size() == numPlayers)
            gameState = GameState.PREGAME;
    }

    /**
     * Get the name of a player in the game (class name).
     */
    public String getPlayerName(int playerNumber)
    {
        return  players.get(playerNumber).getClass().getSimpleName();
    }

    /**
     * Set up the initial deck of cards, allocate chips.
     * Set up player hands to be empty.
     */
    private void initializeGame()
    {
        if (!(gameState == GameState.PREGAME || gameState == GameState.OVER))
            throw new IllegalStateException("Can't initialize game: " + gameState);

        // initial deck
        initializeStartingDeck();

        // setup chips, player hands
        playerChips.clear();
        playerHands.clear();
        for (int x = 0; x < numPlayers; x++)
        {
            playerChips.add(11);
            playerHands.add(new SortedList<>());
        }

        // clear points
        finalScores.clear();
    }

    /**
     * Generate a new starting deck of cards.  Cards are numbered
     * 3 to 35 inclusive, shuffled, 9 removed at random.
     */
    private void initializeStartingDeck()
    {
        cardsInDeck.clear();

        // generate deck of 3-35
        for (int x = 3; x <= 35; x++) {
            cardsInDeck.add(x);
        }

        // remove 9 random cards
        for (int x = 0; x < 9; x++)
        {
            cardsInDeck.remove((int)(Math.random() * cardsInDeck.size()));
        }
        Collections.shuffle(cardsInDeck);
    }

    /**
     * Run through one iteration of the game.
     */
    public void playGame()
    {
        if (gameState != GameState.PREGAME)
            throw new IllegalStateException();

        initializeGame();

        gameState = GameState.PLAYING;

        if (DEBUG) System.out.println("Deck: " + cardsInDeck);
        if (DEBUG) System.out.println("Deck size: " + cardsInDeck.size());

        //System.out.println(getCardScore(cardsInDeck));

        // randomize first player
        int currentPlayer = (int)(Math.random() * numPlayers);

        // initial chips on first card is zero.
        int currentChipsOnCard = 0;

        for (int cardNumOffered : cardsInDeck)
        {
            if (DEBUG) System.out.println("Top card is now " + cardNumOffered);

            boolean taken = false;
            while (!taken)
            {
                if (DEBUG) System.out.println("\n  Offering to player " + currentPlayer + "; chips on card = " + currentChipsOnCard);
                if (DEBUG) System.out.println("  this player has " + playerChips.get(currentPlayer) + " chips.");
                if (DEBUG) System.out.println("    All players' chips = " + playerChips);

                taken = players.get(currentPlayer).offeredCard(cardNumOffered, currentChipsOnCard, playerHands,
                        currentPlayer, playerChips.get(currentPlayer));

                if (!taken && playerChips.get(currentPlayer) > 0)
                {
                    if (DEBUG) System.out.println("  Player says no thanks!");
                    playerChips.set(currentPlayer, playerChips.get(currentPlayer) - 1);
                    currentPlayer++;
                    if (currentPlayer == players.size()) currentPlayer = 0;
                    currentChipsOnCard++;
                }
                else
                {
                    if (!taken) {
                        if (DEBUG) System.out.println("Player MUST take card!");
                        taken = true;
                    }

                    if (DEBUG) System.out.println("  Player takes card.");
                    playerHands.get(currentPlayer).add(cardNumOffered);
                    //Collections.sort(playerHands.get(currentPlayer));
                    playerChips.set(currentPlayer, playerChips.get(currentPlayer) + currentChipsOnCard);
                    currentChipsOnCard = 0;
                }
            }
        }
        if (DEBUG) System.out.println("Final player hands: " + playerHands);
        if (DEBUG) System.out.println("Final player chips: " + playerChips);
        gameState = GameState.OVER;

        // generate scores
        for (int x = 0; x < numPlayers; x++)
        {
            if (DEBUG) System.out.println("Score for player " + x + ": " + (getCardScore(playerHands.get(x)) - playerChips.get(x)));
            finalScores.add(getCardScore(playerHands.get(x)) - playerChips.get(x));
        }
    }

    /**
     * Retrieve the final scores for each player after the game is over.
     */
    public List<Integer> getFinalScores()
    {
        if (gameState != GameState.OVER)
            throw new IllegalStateException();
        return finalScores;
    }

    /**
     * Return the winner of the game.  Ties are broken by lower player number (but this is unlikely).
     */
    public int getWinner()
    {
        if (gameState != GameState.OVER)
            throw new IllegalStateException();

        int minPlayer = 0;
        int minPoints = finalScores.get(0);

        for (int x = 1; x < numPlayers; x++)
        {
            if (finalScores.get(x) < minPoints)
            {
                minPlayer = x;
                minPoints = finalScores.get(x);
            }
        }
        return minPlayer;
    }

    public String getWinnerName() {
        int i = getWinner();
        return getPlayerName(i);
    }

    /**
     * Reset the game to allow another one to be played with the same players.
     */
    public void resetGame()
    {
        initializeGame();

        if (gameState == GameState.OVER)
            gameState = GameState.PREGAME;
    }

    /**
     * Return the "card score" for a final set of cards held by a player.
     * The score is the sum of all the numbers on the cards held, except that a streak of cards
     * counts only the lowest card in each streak.
     */
    public static int getCardScore(SortedList<Integer> cards)
    {
        // Your code here.
        if (cards.size() == 0 ) return 0; 
        int counter = 0; 
        boolean sequential = false; 
         // technically, this has undefined behavior for sequences that start with 1,
        //but since we're dealing with a 3 minimum here, we don't have to worry about it.  
        for (int i = 1; i < cards.size(); i++){
            if ((cards.get(i - 1)) + 1 == cards.get(i)){
                sequential = true; 
            }
            if (!sequential){
                counter+= cards.get(i);
            } else {
                continue; 
            }
        }
        return counter;  // remove this line.
    }

    private static class CardScoreTester
    {
        public static void main(String[] args)
        {
            SortedList<Integer> slist = new SortedList<>();
            slist.add(10);
            slist.add(12);
            System.out.println("Card score for " + slist
                    + " is " + NoThanksGame.getCardScore(slist)); // should be 22
            slist.add(11);
            System.out.println("Card score for " + slist
                    + " is " + NoThanksGame.getCardScore(slist)); // should be 10

            slist.clear();
            // Write more tests here.
        }
    }
}
