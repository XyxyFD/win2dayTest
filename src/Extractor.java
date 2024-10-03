import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Extractor {

    // Define the PokerHand class with gameCode (as an example)
    public static class PokerHand {
        public String blinds;
        public String handNumber;
        public String[] boardCards;

        public List<String> preflopAction;
        public List<String> flopAction;
        public List<String> turnAction;
        public List<String> riverAction;
        public List<String> actions;
        public String pokerSite;
        public String gameFormat;
        public String maxPlayers;
        public String btnSeat;

        public String LJ;
        public String HJ;
        public String CO;
        public String BTN;
        public String SB;
        public String BB;
        public String UTG;
        public String UTG1;
        public String UTG2;

        public double ljStack;
        public double hjStack;
        public double coStack;
        public double btnStack;
        public double sbStack;
        public double bbStack;
        public double utgStack;
        public double utg1Stack;
        public double utg2Stack;
        public PokerHand(String handNumber) {
            this.handNumber = handNumber;
            this.boardCards = new String[5];
            this.actions = new ArrayList<>();
            this.preflopAction = new ArrayList<>();
            this.flopAction = new ArrayList<>();
            this.turnAction = new ArrayList<>();
            this.riverAction = new ArrayList<>();
        }

        // Add other properties and methods as needed
    }

    // Method to extract hands from an XML file
    public static void extract(String filePath) {
        try {
            // Initialize the XML parser
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Extracting game elements from the XML
            NodeList gameList = doc.getElementsByTagName("game");

            // Loop through all game elements
            for (int temp = 0; temp < gameList.getLength(); temp++) {
                Node gameNode = gameList.item(temp);

                if (gameNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameElement = (Element) gameNode;

                    // Extract gamecode from the XML
                    String gameCode = gameElement.getAttribute("gamecode");

                    // Create a new PokerHand object with gameCode
                    PokerHand hand = new PokerHand(gameCode);
                    //hand.boardCards = gameElement.getChildNodes().
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method to run the extraction
    public static void main(String[] args) {
        // Path to the XML file
        String filePath = "src/4150184173.xml";
        extract(filePath);
    }
}


