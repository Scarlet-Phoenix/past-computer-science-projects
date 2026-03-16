
package nothanks.player;

import java.util.List;
import sortedlist.SortedList;


public interface Player {

    /**
     * This function is called by the NoThanksGame class when a card is being offered
     * to an AI player.  The player is given the following information:
     *
     * cardNumber: the number on the card being offered to the player.
     * chipsOnCard: the number of chips currently on the card.
     * playersHands: a list of SortedLists; each SortedList contains a single players' cards.
     * myPlayerNum: the number of the current player being offered the card (used to figure
     *              out which hand is mine in playersHands.
     * myChips: the total number of chips in my hand.  If this is zero, I must take the card.
     *
     * This function should return true to take the card, and false to reject it.
     * Note, if you reject a card (return false) but have no chips, the game will force
     * you to take the card anyway.
     *
     */
    public boolean offeredCard(int cardNumber, int chipsOnCard,
                               List<SortedList<Integer>> playersHands,
                               int myPlayerNum, int myChips);
}
