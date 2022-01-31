package br.edu.ifpe.recife.ldd.prova.questao3;

import br.edu.ifpe.recife.ldd.prova.Product;

import javax.xml.stream.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TerceiraQuestao {
    private static final Logger logger = Logger.getLogger(TerceiraQuestao.class.getName());

    public static String PATH = "src/main/resources/provided/products.xml";
    public static String RESULT_PATH = "src/main/resources/results/question_1_C.xml";


    public static void gerarResultado(HashSet<String> vendors, List<Product> products) {

        try {
            File result = new File(RESULT_PATH);
            OutputStream outputStream = new FileOutputStream(result);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "utf-8");

            XMLStreamWriter out = XMLOutputFactory
                    .newInstance()
                    .createXMLStreamWriter(outputStreamWriter);

            out.writeStartElement("table");

            out.writeStartElement("thead");
            out.writeStartElement("tr");
            out.writeStartElement("th");
            out.writeCharacters("Vendor");
            out.writeEndElement();
            out.writeStartElement("th");
            out.writeCharacters("Name");
            out.writeEndElement();
            out.writeEndElement();
            out.writeEndElement();

            out.writeStartElement("tbody");

            List<Product> filteredByVendor = new ArrayList<>();
            for (String vendor : vendors) {
                out.writeStartElement("tr");
                out.writeStartElement("td");
                out.writeCharacters(vendor);
                out.writeEndElement();

                out.writeStartElement("td");
                out.writeStartElement("ul");

                filteredByVendor = products.stream().filter(p -> p.getVendor().equals(vendor)).collect(Collectors.toList());

                for (Product product : filteredByVendor) {
                    out.writeStartElement("li");
                    out.writeCharacters(product.getName());
                    out.writeEndElement();

                }

                out.writeEndElement();
                out.writeEndElement();
                out.writeEndElement();
            }

            out.writeEndElement();
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
            FileInputStream fileInputStream = new FileInputStream(PATH);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(fileInputStream);

            String name = "";
            String vendor = "";

            HashSet<String> vendors = new HashSet<>();
            List<Product> products = new ArrayList<>();
            Product product;

            while (xmlStreamReader.hasNext()) {
                int evenType = xmlStreamReader.next();

                switch (evenType) {
                    case XMLStreamReader.START_ELEMENT:
                        if (xmlStreamReader.getLocalName().equals("vendor")) {
                            vendor = xmlStreamReader.getElementText();
                        }
                        if (xmlStreamReader.getLocalName().equals("name")) {
                            name = xmlStreamReader.getElementText();
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        if (xmlStreamReader.getLocalName().equals("product")) {
                            product = new Product();
                            product.setName(name);
                            product.setVendor(vendor);
                            products.add(product);
                            vendors.add(vendor);
                        }

                }
            }

            gerarResultado(vendors, products);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error: " + e.getMessage(), e);
        }
    }
}
