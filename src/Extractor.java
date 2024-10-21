import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Extractor {

    // Define the PokerHand class with gameCode (as an example)


    public static List<PokerHand> extract(String filePath) {
        List<PokerHand> pokerHands = new ArrayList<>(); // Liste für PokerHand-Objekte

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
                    hand.blinds = extractBlindsFromGametype(extractBlinds(doc));
                    System.out.println(hand.blinds);
                    pokerHands.add(hand);
                    NodeList roundList = gameElement.getElementsByTagName("round");

                    for (int j = 0; j < roundList.getLength(); j++) {
                        Element roundElement = (Element) roundList.item(j);

                        if ("2".equals(roundElement.getAttribute("no"))) {
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String flopCards = cardsElement.getTextContent();
                            String[] cards = flopCards.split(" ");
                            int index = 0;
                            for (String card : cards) {
                                String value = card.substring(1);
                                if (value.equals("10")) {
                                    value = "T";
                                }
                                char suite = card.charAt(0);
                                value += Character.toLowerCase(suite);
                                hand.boardCards[index] = value;
                                index++;
                                if (index >= hand.boardCards.length) break;
                            }
                        }
                        if ("3".equals(roundElement.getAttribute("no"))) {
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String turnCard = cardsElement.getTextContent();
                            String value = turnCard.substring(1);
                            if (value.equals("10")) {
                                value = "T";
                            }
                            char suite = turnCard.charAt(0);
                            value += Character.toLowerCase(suite);
                            hand.boardCards[3] = value;
                        }
                        if ("4".equals(roundElement.getAttribute("no"))) {
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String riverCard = cardsElement.getTextContent();
                            String value = riverCard.substring(1);
                            if (value.equals("10")) {
                                value = "T";
                            }
                            char suite = riverCard.charAt(0);
                            value += Character.toLowerCase(suite);
                            hand.boardCards[4] = value;
                        }
                    }



                    // PokerHand zur Liste hinzufügen

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return pokerHands; // Rückgabe der Liste von PokerHands
    }
    public static String extractBlinds(Document doc) {
        // Extrahiere das gametype Element
        NodeList gametypeList = doc.getElementsByTagName("gametype");

        if (gametypeList.getLength() > 0) {
            Node gametypeNode = gametypeList.item(0);
            String gametype = gametypeNode.getTextContent();
            return gametype;
        } else {
            System.out.println("Gametype nicht gefunden.");
            return null;
        }
    }
    public static String extractBlindsFromGametype(String gametype) {
        // Regex, um Blinds im Format €0,50/€1 zu finden
        String regex = "€[0-9,]+/[€0-9,]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(gametype);

        if (matcher.find()) {
            return matcher.group(); // Gib die gefundene Blind-Information zurück als zb €0,50/€1
        } else {
            return "Keine Blinds gefunden";
        }
    }


    // Main method to run the extraction
    public static void main(String[] args) {
        // Path to the XML file
        String filePath = "src/4150184173.xml";
        extract(filePath);

    }
}