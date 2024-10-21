import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public  class PokerHand {
    public String blinds;
    public String handNumber; //erledigt
    public String[] boardCards; //erledigt

    public List<String> preflopAction;
    public List<String> flopAction;
    public List<String> turnAction;
    public List<String> riverAction;
    //public List<String> actions;
    //public String pokerSite;
    //public String gameFormat;
    public String maxPlayers;
    public String btnSeat;

    public String LJ;
    public String HJ;
    public String CO;
    public String BTN;
    public String SB;
    public String BB;
    //public String UTG;
    //public String UTG1;
    //public String UTG2;


    public PokerHand(String handNumber) {
        this.handNumber = handNumber;
        this.boardCards = new String[5];
        this.preflopAction = new ArrayList<>();
        this.flopAction = new ArrayList<>();
        this.turnAction = new ArrayList<>();
        this.riverAction = new ArrayList<>();
    }


    // Add other properties and methods as needed
}
