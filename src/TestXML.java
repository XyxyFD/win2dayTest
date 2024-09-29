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
                Node gameNode = gameList.item(i);

                // Check if the node is an element
                if (gameNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameElement = (Element) gameNode;

                    // 7. Access the 'gamecode' attribute
                    String gameCode = gameElement.getAttribute("gamecode");

                    // 8. Print or store the gamecode
                    System.out.println("Gamecode: " + gameCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
