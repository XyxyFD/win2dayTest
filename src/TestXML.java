import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TestXML {



    public static void main(String[] args) {

        try {
            // 1. Create a new instance of the DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 2. Use the factory to create a DocumentBuilder object
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 3. Parse the XML file and load it into a Document object
            File xmlFile = new File("src/4150184173.xml");  // Update with your actual file path
            Document doc = builder.parse(xmlFile);

            // 4. Normalize the XML structure (optional, but recommended)
            doc.getDocumentElement().normalize();

            // 5. Get the NodeList of all <game> elements
            NodeList gameList = doc.getElementsByTagName("game");

            // 6. Loop through the game elements and access the 'gamecode' attribute
            for (int i = 0; i < gameList.getLength(); i++) {
                String[] boardCards = new String[5];
                Node gameNode = gameList.item(i);

                // Check if the node is an element
                if (gameNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameElement = (Element) gameNode;

                    // 7. Access the 'gamecode' attribute
                    String gameCode = gameElement.getAttribute("gamecode");

                    // 8. Print or store the gamecode
                    System.out.println("Gamecode: " + gameCode);
                    NodeList roundList = gameElement.getElementsByTagName("round");
                    for (int j = 0; j < roundList.getLength(); j++) {
                        Element roundElement = (Element) roundList.item(j);

                        // Überprüfen, ob es sich um Runde no="2" (Flop) handelt
                        if ("2".equals(roundElement.getAttribute("no"))) {
                            // 6. Zugriff auf das <cards> Element in der Runde
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String flopCards = cardsElement.getTextContent();
                            System.out.println("Flopcards: " + flopCards);
                            String[] cards = flopCards.split(" ");
                            int index = 0;
                            for (String card : cards) {
                                // Extrahiere den Kartenwert
                                String value = card.substring(1); // Die Karte ohne den ersten Buchstaben (z.B. H, C)
                                // Behandle die 10 separat, um sicherzustellen, dass sie als "T" gespeichert wird
                                if (value.equals("10")) {
                                    value = "T";
                                }
                                char suite = card.charAt(0);
                                value += Character.toLowerCase(suite);
                                boardCards[index] = value;
                                index++;
                                if (index >= boardCards.length) break; // Sicherheitshalber verhindern, dass das Array überschrieben wird
                            }

                        }
                        if ("3".equals(roundElement.getAttribute("no"))) {
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String turnCard = cardsElement.getTextContent();
                            System.out.println("Turncard: " + turnCard);
                            String value = turnCard.substring(1); // Die Karte ohne den ersten Buchstaben (z.B. H, C)
                            // Behandle die 10 separat, um sicherzustellen, dass sie als "T" gespeichert wird
                            if (value.equals("10")) {
                                value = "T";
                            }
                            char suite = turnCard.charAt(0);
                            value += Character.toLowerCase(suite);
                            boardCards[3] = value;
                        }
                        if ("4".equals(roundElement.getAttribute("no"))) {
                            Element cardsElement = (Element) roundElement.getElementsByTagName("cards").item(0);
                            String riverCard = cardsElement.getTextContent();
                            System.out.println("Rivercard: " + riverCard);
                            String value = riverCard.substring(1); // Die Karte ohne den ersten Buchstaben (z.B. H, C)
                            // Behandle die 10 separat, um sicherzustellen, dass sie als "T" gespeichert wird
                            if (value.equals("10")) {
                                value = "T";
                            }
                            char suite = riverCard.charAt(0);
                            value += Character.toLowerCase(suite);
                            boardCards[4] = value;
                        }

                    }

                }
                for (int a = 0; a < boardCards.length; a++) {
                    System.out.println(boardCards[a]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
