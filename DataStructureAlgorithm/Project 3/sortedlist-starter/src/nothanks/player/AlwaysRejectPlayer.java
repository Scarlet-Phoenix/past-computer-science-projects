package nothanks.player;

import sortedlist.SortedList;

import java.util.List;

public class AlwaysRejectPlayer implements Player {
    public boolean offeredCard(int cardNumber, int chipsOnCard,
                               List<SortedList<Integer>> playersHands,
                               int myPlayerNum, int myChips)
    {
        return false; // reject the card
    }
}
