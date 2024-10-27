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

    private static String blinds = "Blinds not defined";
    public static List<PokerHand> extract(String filePath) {
        List<PokerHand> pokerHands = new ArrayList<>(); // Liste für PokerHand-Objekte

        try {
            // Initialize the XML parser
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            blinds = extractBlindsFromGametype(extractGametype(doc));

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
                    extractPositionByPlayername(gameElement, hand);
                    hand.blinds = blinds;
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
                    addActions(gameElement, hand);



                    // PokerHand zur Liste hinzufügen

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return pokerHands; // Rückgabe der Liste von PokerHands
    }
    public static String extractGametype(Document doc) {
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
    public static void extractPositionByPlayername(Element gameElement, PokerHand hand) {
        NodeList roundList = gameElement.getElementsByTagName("round");

        // Extract gamecode from the XML
        String gameCode = gameElement.getAttribute("gamecode");
        //System.out.println(gameCode);

        // Schleife durch alle Runden (round), um die Aktionen zu analysieren
        for (int i = 0; i < roundList.getLength(); i++) {
            Element roundElement = (Element) roundList.item(i);

            // Wir suchen nach der Preflop-Runde (round no="0"), in der die Blinds gesetzt werden
            if ("0".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");


                // Schleife durch alle Aktionen in der Preflop-Runde
                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    // Extrahiere den Small Blind (action no="1")
                    if ("1".equals(actionElement.getAttribute("no"))) {
                        String smallBlindPlayer = actionElement.getAttribute("player");
                        hand.SB = smallBlindPlayer; // Setze den Spieler als Small Blind
                    }

                    // Extrahiere den Big Blind (action no="2")
                    if ("2".equals(actionElement.getAttribute("no"))) {
                        String bigBlindPlayer = actionElement.getAttribute("player");
                        hand.BB = bigBlindPlayer; // Setze den Spieler als Big Blind
                    }

                    // Zuweisung der weiteren Positionen basierend auf der Anzahl der Spieler



                }
            }else if ("1".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");
                int playerCount = gameElement.getElementsByTagName("player").getLength();

                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    // Extrahiere Spieler mit `no="3"` und weise die Positionen zu
                    if ("3".equals(actionElement.getAttribute("no"))) {
                        String playerName = actionElement.getAttribute("player");
                        switch (playerCount) {
                            case 3:
                                hand.BTN = playerName;
                                break;
                            case 4:
                                hand.CO = playerName;
                                break;
                            case 5:
                                hand.HJ = playerName;
                                break;
                            case 6:
                                hand.LJ = playerName;
                                break;
                        }
                    }

                    // Extrahiere Spieler mit `no="4"` und weise die Positionen zu
                    if ("4".equals(actionElement.getAttribute("no"))) {
                        String playerName = actionElement.getAttribute("player");
                        switch (playerCount) {
                            case 4:
                                hand.BTN = playerName;
                                break;
                            case 5:
                                hand.CO = playerName;
                                break;
                            case 6:
                                hand.HJ = playerName;
                                break;
                        }
                    }

                    // Extrahiere Spieler mit `no="5"` und weise die Positionen zu
                    if ("5".equals(actionElement.getAttribute("no"))) {
                        String playerName = actionElement.getAttribute("player");
                        if (playerCount == 5) {
                            hand.BTN = playerName;
                        } else if (playerCount == 6) {
                            hand.CO = playerName;
                        }
                    }

                    // Extrahiere Spieler mit `no="6"` und weise die Positionen zu
                    if ("6".equals(actionElement.getAttribute("no"))) {
                        String playerName = actionElement.getAttribute("player");

                        if (playerCount == 6) {
                            hand.BTN = playerName;
                        }
                    }
                }


            }
        }

    }
    public static String addPosToAction(String playerName, PokerHand currentHand) {
        if (playerName.equals(currentHand.LJ)) {
            return "LJ";
        } else if (playerName.equals(currentHand.HJ)) {
            return "HJ";
        } else if (playerName.equals(currentHand.CO)) {
            return "CO";
        } else if (playerName.equals(currentHand.BTN)) {
            return "BTN";
        } else if (playerName.equals(currentHand.SB)) {
            return "SB";
        } else if (playerName.equals(currentHand.BB)) {
            return "BB";
        } else {
            return "Unknown";
        }
    }
    public static String processActionType(String type) {
        switch (type) {
            case "0":
                return "f";
            case "3":
                return "c";
            case "23":
                return "r";
            case "4":
                return "x";
            case "5":
                return "b";
            default:
                return "No Actiontype";
        }
    }
    public static double convertSumToBB(String sum, PokerHand currenthand, String blinds) {
        // Bereinige den sum-String, entferne das €-Zeichen und ersetze das Komma durch einen Punkt
        String cleanSum = sum.replace("€", "").replace(",", ".");
        double sumValue = Double.parseDouble(cleanSum);

        // Extrahiere den Big Blind-Wert aus dem blinds-String
        String bigBlindValueString = blinds.split("/")[1].replace("€", "").replace(",", ".");
        double bigBlindValue = Double.parseDouble(bigBlindValueString);

        // Berechne den Betrag in BB und gib das Ergebnis zurück
        return sumValue / bigBlindValue;
    }



    public static void addActions(Element gameElement, PokerHand currentHand){
        NodeList roundList = gameElement.getElementsByTagName("round");

        for (int i = 0; i < roundList.getLength(); i++) {
            Element roundElement = (Element) roundList.item(i);

            // Wenn die Runde "no=1" ist, dann extrahiere die Aktionen
            if ("1".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");

                // Schleife durch alle action Elemente innerhalb von round no="1"
                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    String player = actionElement.getAttribute("player");
                    String sum = actionElement.getAttribute("sum");
                    String type = actionElement.getAttribute("type");

                    String position = addPosToAction(player, currentHand);
                    String actionType = processActionType(type);
                    String betSizeInBB = String.valueOf(convertSumToBB(sum, currentHand, currentHand.blinds));
                    if(actionType.equals("b") || actionType.equals("r")){
                        currentHand.preflopAction.add(position + "_" + actionType + betSizeInBB);
                    }else{
                        currentHand.preflopAction.add(position + "_" + actionType);
                    }

                    //System.out.println("Player: " + player + ", Sum: " + sum + ", Type: " + type);
                }
            }
            else if ("2".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");

                // Schleife durch alle action Elemente innerhalb von round no="1"
                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    String player = actionElement.getAttribute("player");
                    String sum = actionElement.getAttribute("sum");
                    String type = actionElement.getAttribute("type");

                    String position = addPosToAction(player, currentHand);
                    String actionType = processActionType(type);
                    String betSizeInBB = String.valueOf(convertSumToBB(sum, currentHand, currentHand.blinds));
                    if(actionType.equals("b") || actionType.equals("r")){
                        currentHand.flopAction.add(position + "_" + actionType + betSizeInBB);
                    }else{
                        currentHand.flopAction.add(position + "_" + actionType);
                    }

                    //System.out.println("Player: " + player + ", Sum: " + sum + ", Type: " + type);
                }
            }
            else if ("3".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");

                // Schleife durch alle action Elemente innerhalb von round no="1"
                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    String player = actionElement.getAttribute("player");
                    String sum = actionElement.getAttribute("sum");
                    String type = actionElement.getAttribute("type");

                    String position = addPosToAction(player, currentHand);
                    String actionType = processActionType(type);
                    String betSizeInBB = String.valueOf(convertSumToBB(sum, currentHand, currentHand.blinds));
                    if(actionType.equals("b") || actionType.equals("r")){
                        currentHand.turnAction.add(position + "_" + actionType + betSizeInBB);
                    }else{
                        currentHand.turnAction.add(position + "_" + actionType);
                    }

                    //System.out.println("Player: " + player + ", Sum: " + sum + ", Type: " + type);
                }
            }
            else if ("4".equals(roundElement.getAttribute("no"))) {
                NodeList actionList = roundElement.getElementsByTagName("action");

                // Schleife durch alle action Elemente innerhalb von round no="1"
                for (int j = 0; j < actionList.getLength(); j++) {
                    Element actionElement = (Element) actionList.item(j);

                    String player = actionElement.getAttribute("player");
                    String sum = actionElement.getAttribute("sum");
                    String type = actionElement.getAttribute("type");

                    String position = addPosToAction(player, currentHand);
                    String actionType = processActionType(type);
                    String betSizeInBB = String.valueOf(convertSumToBB(sum, currentHand, currentHand.blinds));
                    if(actionType.equals("b") || actionType.equals("r")){
                        currentHand.riverAction.add(position + "_" + actionType + betSizeInBB);
                    }else{
                        currentHand.riverAction.add(position + "_" + actionType);
                    }

                    //System.out.println("Player: " + player + ", Sum: " + sum + ", Type: " + type);
                }
            }
        }
    }





    // Main method to run the extraction
    public static void main(String[] args) {
        // Path to the XML file
        String filePath = "src/4150184173.xml";
        List<PokerHand> pokerHands = extract(filePath);
        for (PokerHand hand: pokerHands
             ) {
            System.out.println(hand);
        }


    }


}