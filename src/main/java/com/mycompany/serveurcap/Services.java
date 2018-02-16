/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serveurcap;

import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Pauline
 */
public class Services {
    
    public World readWorldFromXml(){
        World world = new World();
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");;
            world = (World) u.unmarshal(input);
        }
        catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            e.printStackTrace();
        }
        return world;
    }
   
    
    public void saveWorldToXml(World world) throws JAXBException, FileNotFoundException{
        JAXBContext cont = JAXBContext.newInstance(World.class);
        OutputStream output = new FileOutputStream("World.xml");
        Marshaller m = cont.createMarshaller();
        m.marshal(output, new File("newworld.xml"));        
    }
    
}
