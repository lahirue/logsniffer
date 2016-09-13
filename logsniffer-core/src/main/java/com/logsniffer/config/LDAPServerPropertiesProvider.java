package com.logsniffer.config;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LDAPServerPropertiesProvider{

    private static HashMap<String, String> propertyMap;

    public static void initialize(){
        propertyMap= new HashMap<String, String>();
        try {

            File file = new File("./conf_files/ldap_config.xml");
            parseXml(file);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void parseXml(File file){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("property");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    propertyMap.put(eElement.getAttribute("id"), eElement.getElementsByTagName("value").item(0).getTextContent());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap propertyMap(){
        return propertyMap;
    }

}