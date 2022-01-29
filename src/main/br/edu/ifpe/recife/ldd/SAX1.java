package br.edu.ifpe.recife.ldd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAX1 extends DefaultHandler {
    private boolean isName;
    private boolean isLine;
    private boolean isScale;
    private boolean isVendor;
    private boolean isDescription;
    private boolean isQuantityInStock;
    private boolean isBuyPrice;
    private boolean isMsrp;

    private List<Product> products = new ArrayList<>();
    private Product product;

    public static void print(Object obj) {
        System.out.println(obj.toString());
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
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
            isMsrp = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value =  new String(ch, start, length);

        if (isName) {
            this.product = new Product();
            this.product.setName(value);
            isName = false;
        }
        if (isLine) {
            this.product.setLine(value);
            isLine = false;
        }
        if (isScale) {
            this.product.setScale(value);
            isScale = false;
        }
        if (isVendor) {
            this.product.setVendor(value);
            isVendor = false;
        }
        if (isDescription) {
            this.product.setDescription(value);
            isDescription = false;
        }
        if (isQuantityInStock) {
            this.product.setQuantityInStock(Integer.valueOf(value));
            isQuantityInStock = false;
        }
        if (isBuyPrice) {
            this.product.setBuyPrice(Double.valueOf(value));
            isBuyPrice = false;
        }
        if (isMsrp) {
            this.product.setMsrp(Double.valueOf(value));
            this.products.add(this.product);
            isMsrp = false;
        }
    }


    @Override
    public void endDocument() throws SAXException {
        print(products);
    }

    public static void main(String[] args) {
        File inputFile = new File("src/main/resources/alterede.xml");
        SAX1 sax1 = new SAX1();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputFile, sax1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}