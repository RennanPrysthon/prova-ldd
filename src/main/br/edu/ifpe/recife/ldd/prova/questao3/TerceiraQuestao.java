package br.edu.ifpe.recife.ldd.prova.questao3;

import br.edu.ifpe.recife.ldd.prova.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TerceiraQuestao {
    public static String PATH = "src/main/resources/provided/products.xml";
    public static String RESULT_PATH = "src/main/resources/results/question_1_C.xml";

    public static Element createThElement(Document out, String content) {
        Element th = out.createElement("th");
        th.setTextContent(content);
        return th;
    }

    public static void gerarTabelaDetalhada(TransformerFactory transformerFactory, DocumentBuilder db, List<Product> products) {

        List<String> fornecedores = products
            .stream()
            .map(Product::getVendor)
            .sorted()
            .collect(Collectors.toList());

        fornecedores = new ArrayList<>(new HashSet<>(fornecedores));


        try {
            Document out = db.newDocument();
            Element table = out.createElement("table");
            Element thead = out.createElement("thead");
            Element tr = out.createElement("tr");
            tr.appendChild(createThElement(out, "Vendor"));
            tr.appendChild(createThElement(out, "Name"));

            thead.appendChild(tr);
            table.appendChild(thead);

            Element tbody = out.createElement("tbody");

            for (String fornecedor: fornecedores) {
                tr = out.createElement("tr");
                Element td = out.createElement("td");
                td.setTextContent(fornecedor);

                tr.appendChild(td);

                td = out.createElement("td");
                Element ul = out.createElement("ul");

                List<Product> filteredProducts = products
                    .stream()
                    .filter(product -> product.getVendor().equals(fornecedor))
                    .collect(Collectors.toList());

                for (Product filteredProduct: filteredProducts) {
                    Element li = out.createElement("li");
                    li.setTextContent(filteredProduct.getName());
                    ul.appendChild(li);
                }

                td.appendChild(ul);

                tr.appendChild(td);

                tbody.appendChild(tr);
            }

            table.appendChild(tbody);
            out.appendChild(table);

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

            gerarTabelaDetalhada(transformerFactory, db, products);
        } catch(SAXException | ParserConfigurationException | IOException e) {
            System.out.println("Error: " + e.getClass().getName() + " | " +  e.getMessage());
        }
    }
}
