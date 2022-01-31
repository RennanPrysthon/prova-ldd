package br.edu.ifpe.recife.ldd.prova.questao2;

import br.edu.ifpe.recife.ldd.prova.Product;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SegundaQuestao extends DefaultHandler {
    private static final Logger logger = Logger.getLogger(SegundaQuestao.class.getName());

    public static String PATH = "src/main/resources/provided/products.xml";
    public static String RESULT_PATH = "src/main/resources/results/question_1_B.xml";

    public boolean isName;
    public boolean isLine;
    public boolean isScale;
    public boolean isVendor;
    public boolean isDescription;
    public boolean isQuantityInStock;
    public boolean isBuyPrice;
    public boolean isMSRP;

    public Product product;
    public List<Product> products = new ArrayList<>();

    @Override
    public void startDocument() {
        products = new ArrayList<>();
        product = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("name")) {
            isName = true;
        }
        if (qName.equals("line")) {
            isLine = true;
        }
        if (qName.equals("scale")) {
            isScale = true;
        }
        if (qName.equals("vendor")) {
            isVendor = true;
        }
        if (qName.equals("description")) {
            isDescription = true;
        }
        if (qName.equals("quantityInStock")) {
            isQuantityInStock = true;
        }
        if (qName.equals("buyPrice")) {
            isBuyPrice = true;
        }
        if (qName.equals("MSRP")) {
            isMSRP = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {

        String value = new String(ch, start, length);

        if (isName) {
            product = new Product();
            product.setName(value);
            isName = false;
        }
        if (isLine) {
            product.setLine(value);
            isLine = false;
        }
        if (isScale) {
            product.setScale(value);
            isScale = false;
        }
        if (isVendor) {
            product.setVendor(value);
            isVendor = false;
        }
        if (isDescription) {
            product.setDescription(value);
            isDescription = false;
        }
        if (isQuantityInStock) {
            product.setQuantityInStock(Integer.valueOf(value));
            isQuantityInStock = false;
        }
        if (isBuyPrice) {
            product.setBuyPrice(Double.valueOf(value));
            isBuyPrice = false;
        }
        if (isMSRP) {
            product.setMsrp(Double.valueOf(value));
            isMSRP = false;
            products.add(product);
        }
    }

    @Override
    public void endDocument() {
        gerarTabelaPercentual(products);
    }

    public static void gerarTabelaPercentual(List<Product> products) {
        try {
            File result = new File(RESULT_PATH);
            OutputStream outputStream = new FileOutputStream(result);

            XMLStreamWriter out = XMLOutputFactory
                .newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(outputStream, "utf-8")
                );

            out.writeStartDocument();
            out.writeStartElement("ol");

            products =  products
                .stream()
                .peek(product -> {
                    double percent = ((product.getBuyPrice() * 100) / product.getMsrp());
                    product.setPercent(percent);
                })
                .sorted(Comparator.comparing(Product::getPercent).reversed())
                .collect(Collectors.toList());

            for (Product product : products) {
                out.writeStartElement("li");

                String value = product.getName() +
                    " (" +
                        Math.round(product.getPercent()) +
                    "%)";

                out.writeCharacters(value);
                out.writeEndElement();
            }

            out.writeEndElement();
            out.writeEndDocument();
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File(PATH);
            SegundaQuestao handler = new SegundaQuestao();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        }
    }
}
