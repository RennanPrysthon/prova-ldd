package br.edu.ifpe.recife.ldd.prova.questao2;

import br.edu.ifpe.recife.ldd.prova.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SegundaQuestao {
    public static String PATH = "src/main/resources/provided/products.xml";
    public static String RESULT_PATH = "src/main/resources/results/question_1_B.xml";

    public static void gerarTabelaPercentual(TransformerFactory transformerFactory, DocumentBuilder db, List<Product> products) {

        try {
            Document out = db.newDocument();
            Element ol = out.createElement("ol");

            products
                    .stream()
                    .peek(product -> {
                        double percent= ((product.getBuyPrice() * 100) / product.getMsrp());
                        product.setPercent(percent);
                    })
                    .sorted(Comparator.comparing(Product::getPercent).reversed())
                    .forEach(product -> {
                        Element li = out.createElement("li");

                        StringBuilder value = new StringBuilder();

                        value.append(product.getName());
                        value.append(" (");
                        value.append(Math.round(product.getPercent()));
                        value.append("%)");

                        li.setTextContent(value.toString());
                        ol.appendChild(li);
                    });


            out.appendChild(ol);

            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(out);

            File resultFile = new File(RESULT_PATH);

            StreamResult result = new StreamResult(resultFile);

            transformer.transform(source, result);

        } catch (Exception e) {
            System.out.println("Error: " + e.getClass().getName() + " | " +  e.getMessage());
        }

    }

    public static void main(String[] args) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            File file = new File(PATH);

            Document doc = db.parse(file);

            NodeList list = doc.getElementsByTagName("product");
            List<Product> products = new ArrayList<>();

            for (int i = 0; i < list.getLength(); i++) {
                Element entry = (Element) list.item(i);
                Element name = (Element) entry.getElementsByTagName("name").item(0);
                Element line = (Element) entry.getElementsByTagName("line").item(0);
                Element scale = (Element) entry.getElementsByTagName("scale").item(0);
                Element vendor = (Element) entry.getElementsByTagName("vendor").item(0);
                Element description = (Element) entry.getElementsByTagName("description").item(0);
                Element quantityInStock = (Element) entry.getElementsByTagName("quantityInStock").item(0);
                Element buyPrice = (Element) entry.getElementsByTagName("buyPrice").item(0);
                Element MSRP = (Element) entry.getElementsByTagName("MSRP").item(0);
                Product product = new Product(
                        name.getTextContent(),
                        line.getTextContent(),
                        scale.getTextContent(),
                        vendor.getTextContent(),
                        description.getTextContent(),
                        Integer.valueOf(quantityInStock.getTextContent()),
                        Double.valueOf(buyPrice.getTextContent()),
                        Double.valueOf(MSRP.getTextContent())
                );
                products.add(product);
            }

            gerarTabelaPercentual(transformerFactory, db, products);
        } catch(SAXException | ParserConfigurationException | IOException e) {
            System.out.println("Error: " + e.getClass().getName() + " | " +  e.getMessage());
        }
    }
}
