package nothanks.player;

import java.util.ArrayList;
import java.util.List;
import sortedlist.SortedList;

public class RushDoublePlayer implements Player {

    protected enum ACTIONS
    {
        PREDICT_PLAY, // ADD UP, CONSIDER NEXT OPTION
        HOLD, // PASS CARD AND TAKE CHIP
        RUSH_HOLD, // LOW CHIPS, BUT STILL HOLD. UPDATE CONSIDER TO LOOK FOR NEXT CARD    
        TAKE, // TAKE CARD AND CHIPS.  
        RUSH_TAKE, // LOTS OF CHIPS ON CARD, TAKE FOR RESOURCES

    }

    protected enum CONSIDER
    {
        LOOK_LOW, // LOOK TO TAKE NEXT LOW
        LOOK_HIGH, // LOOK TO TAKE NEXT HIGH
        LOOK_NEXT, // LOOK TO TAKE NEXT
        CHECK_BIAS, // RECHECK WHAT CARD WE'RE LOOKING FOR
        RANDOM, // if all else fails, pick a random option.  

    }
    protected ArrayList<Integer> cardsSeen = new ArrayList<>(); 
    protected ACTIONS action = ACTIONS.TAKE;
    protected CONSIDER consider = CONSIDER.RANDOM;  


    
    public boolean offeredCard(int cardNumber, int chipsOnCard, List<SortedList<Integer>> playersHands,
                               int myPlayerNum, int myChips)
    {
        SortedList<Integer> myCards = playersHands.get(myPlayerNum);
    
        if (myChips == 0){
              action = ACTIONS.TAKE;
            return true;    // if we have no chips, we gotta take. 
        }

        if (myChips <= 3)
        {
            consider = CONSIDER.LOOK_NEXT; 
        }

        
        

        


        switch (this.action)
        {
            case ACTIONS.TAKE -> {
                return true;
            }
            case ACTIONS.RUSH_TAKE -> {
                consider = CONSIDER.LOOK_HIGH; return true; 
            }
            case ACTIONS.HOLD -> {
                return false;
            }
            case ACTIONS.RUSH_HOLD -> {
                consider = CONSIDER.LOOK_LOW; return false;
            }
            default -> {
                consider = CONSIDER.LOOK_NEXT; return false;
            }
        }
    }
}
