package br.edu.ifpe.recife.ldd.prova.questao1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PrimeiraQuestao {
    public static String PATH = "src/main/resources/provided/products.xml";
    public static String RESULT_PATH = "src/main/resources/results/question_1.xml";


    public static Element createThElement(Document out, String value) {
        Element th = out.createElement("th");
        th.setTextContent(value);
        return th;
    }


    public static void main(String[] args) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

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

            products.sort(Comparator.comparing(p -> p.getName()));

            Document out = db.newDocument();
            Element table= out.createElement("table");
            Element tHead = out.createElement("thead");
            Element tr = out.createElement("tr");

            tr.appendChild(createThElement(out, "Name"));
            tr.appendChild(createThElement(out, "Line"));
            tr.appendChild(createThElement(out, "Vendor"));
            tr.appendChild(createThElement(out, "Quantity in Stock"));
            tr.appendChild(createThElement(out, "Buy Price"));

            tHead.appendChild(tr);
            table.appendChild(tHead);

            Element tBody = out.createElement("tbody");

            for(Product product: products) {
                tr = out.createElement("tr");

                tr.appendChild(createThElement(out, product.getName()));
                tr.appendChild(createThElement(out, product.getLine()));
                tr.appendChild(createThElement(out, product.getVendor()));
                tr.appendChild(createThElement(out, String.valueOf(product.getQuantityInStock())));
                tr.appendChild(createThElement(out, String.valueOf(product.getBuyPrice())));

                tBody.appendChild(tr);
            }

            table.appendChild(tBody);

            out.appendChild(table);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(out);

            File resultFile = new File(RESULT_PATH);

            StreamResult result = new StreamResult(resultFile);

            transformer.transform(source, result);

        } catch(TransformerException | ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Error: " + e.getClass().getName() + " | " +  e.getMessage());
        }
    }
}