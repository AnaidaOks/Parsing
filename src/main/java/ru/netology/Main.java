package ru.netology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.*;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);

        try(Writer writer = new FileWriter("data.json")) {
            writer.write(json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<Employee> listXml = parseXML("data.xml");
        String jsonFromXml = listToJson(listXml);

        try(Writer writer = new FileWriter("data2.json")) {
            writer.write(jsonFromXml);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String jsonFromJson = readString("data2.json");

        List<Employee> listJson = jsonToList(jsonFromJson);

        listJson.stream().forEach(System.out::println);
    }

    public static List<Employee> parseCSV(String[] column, String fileName) {
        List<Employee> employee = new ArrayList<>();
        try {

            CSVReader reader = new CSVReader(new FileReader(fileName));
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setColumnMapping(column);
            strategy.setType(Employee.class);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy).build();
            employee = csvToBean.parse();

        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        return employee;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        String json = gson.toJson(list, listType);

        return json;
    }

    public static List<Employee> parseXML(String filename) {
        List<Employee> employee = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filename));
            Node root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (Node.ELEMENT_NODE == children.item(i).getNodeType() && "employee".equals(children.item(i).getNodeName())) {
                    NodeList attrs = children.item(i).getChildNodes();
                    long id = 0;
                    String fName = "";
                    String lName = "";
                    String country = "";
                    int age = 0;

                    for (int y = 0; y < attrs.getLength(); y++) {
                        Node node = attrs.item(y);
                        if ("id".equals(node.getNodeName())) {
                            id = Integer.parseInt(node.getTextContent());
                        }

                        if ("firstName".equals(node.getNodeName())) {
                            fName = node.getTextContent();
                        }

                        if ("lastName".equals(node.getNodeName())) {
                            lName = node.getTextContent();
                        }

                        if ("country".equals(node.getNodeName())) {
                            country = node.getTextContent();
                        }

                        if ("age".equals(node.getNodeName())) {
                            age = Integer.parseInt(node.getTextContent());
                        }
                    }

                    Employee newEmployee = new Employee(id, fName, lName, country, age);
                    employee.add(newEmployee);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return employee;
    }

    public static String readString(String filename) {
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            json = reader.readLine();
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return  json;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> employee = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(json).getAsJsonArray();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        for (JsonElement obj : jsonArray) {
            Employee empl = gson.fromJson(obj, Employee.class);
            employee.add(empl);
        }
        return  employee;
    }
}