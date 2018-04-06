/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serveurcap;

import generated.PallierType;
import generated.ProductType;
import generated.ProductsType;
import generated.TyperatioType;
import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Pauline
 */
public class Services {
    
    private World world;
    private JAXBContext cont;
    private Unmarshaller u;
    
    public World readWorldFromXml(String username) throws JAXBException {
        //System.out.println("user" + username);
        try {
            cont = JAXBContext.newInstance(World.class);
            u = cont.createUnmarshaller();
        } catch (JAXBException e) {
            System.err.println(e.getMessage());
            this.world = null;
        }
        
        try {
            world = (World) u.unmarshal(new File(username + "-world.xml"));
            //System.out.println("pas de fichier");
        } catch (UnmarshalException e) {
            InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
            world = (World) u.unmarshal(input);
            //System.out.println(username);
        }
        
        return world;
    }
   
    public void saveWorldToXml(World world, String pseudo) throws JAXBException, FileNotFoundException{
        world.setLastupdate(System.currentTimeMillis());
        calScore(world);
        Marshaller m = cont.createMarshaller();
        m.marshal(world, new File(pseudo + "-world.xml"));        
    }
    
    private World getWorld(String username) throws JAXBException, FileNotFoundException {
        world = readWorldFromXml(username);
        saveWorldToXml(world,username);
        return world;
    }
    
    private ProductType findProductById(World world, int id) {
        ProductType prod = null;
        List<ProductType> listProducts= world.getProducts().getProduct();
        for (ProductType product : listProducts) {
            if (product.getId()==id){
                prod = product;
            }
        }
        return prod;
    } 
    
    private PallierType findManagerByName(World world, String name) {
        PallierType pal = null;
        List<PallierType> listPalliers= world.getManagers().getPallier();
        for (PallierType pallier : listPalliers) {
            if (pallier.getName().equals(name)){
                pal = pallier;
            }
        }
        return pal;    
    }
    
    public PallierType findAngelByName(World world, String name) {
        PallierType angel = null;
        List<PallierType> listAngels = world.getAngelupgrades().getPallier();
        for (PallierType pallier : listAngels) {
            if (pallier.getName().equals(name)) {
                angel = pallier;
            }
        }
        return angel;
    }
    
    private PallierType findUpgradeByName(World world, String name) {
        PallierType upgrade = null;
        List<PallierType> listPalliers= world.getUpgrades().getPallier();
        for (PallierType pallier : listPalliers) {
            if (pallier.getName().equals(name)){
                upgrade = pallier;
            }
        }
        return upgrade;     
    }
    
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, FileNotFoundException, InterruptedException {
        world = getWorld(username);
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) { 
            return false;
        }
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            if(product.getTimeleft() == 0){
                world.setMoney(world.getMoney() - (product.getCout() * ((1 - Math.pow(product.getCroissance(), qtchange)) / (1 - product.getCroissance()))));
                product.setQuantite(newproduct.getQuantite()+qtchange);
                product.setCout(newproduct.getCout());
            } else {
                Thread.sleep(product.getVitesse()+100);
                world.setMoney(world.getMoney()+product.getRevenu()*product.getQuantite());
            }
            List<PallierType> listUnlocksAllProducts = world.getAllunlocks().getPallier();
            List<ProductType> listProducts = world.getProducts().getProduct();
            List<PallierType> pallierProduct = product.getPalliers().getPallier();
            for (PallierType unlock : listUnlocksAllProducts) {
                if(unlock.isUnlocked()==false){
                    int nbProduit = 0;
                    for (ProductType prod: listProducts) {
                        if ((prod.getQuantite()) >= unlock.getSeuil()) {
                            nbProduit = nbProduit + 1;
                        }
                    }
                    if (nbProduit == 6) {
                        unlock.setUnlocked(true);
                        for (ProductType prod : listProducts) {
                            updateBonus(prod, unlock);
                        }
                    } 
                }
            }  
            for (PallierType pallier : pallierProduct){
                if(product.getQuantite()>=pallier.getSeuil()){
                    updateBonus(product, pallier);
                    pallier.setUnlocked(true);
                }
            }
        }else {
            product.setTimeleft(newproduct.getVitesse());
        }
        saveWorldToXml(world, username);
        return true;
    }

    public Boolean updateManager(String username, PallierType newmanager) throws JAXBException, FileNotFoundException {
        world = getWorld(username);
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        newmanager.setUnlocked(true);
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        product.setManagerUnlocked(true);
        world.setMoney(world.getMoney() - manager.getSeuil());
        saveWorldToXml(world,username);
        return true;
    }    

    void updateUpgrade(String username, PallierType newUpgrade) throws JAXBException, FileNotFoundException {
        world = getWorld(username);
        PallierType upgrade = findUpgradeByName(world, newUpgrade.getName());
        upgrade.setUnlocked(true);
        List<ProductType> listProducts = world.getProducts().getProduct();
        for (ProductType product : listProducts){
            if (upgrade.getIdcible() > 0 && upgrade.getIdcible()==product.getId()) {
                updateBonus(product, upgrade);
            } if (upgrade.getIdcible() == 0) {
                updateBonus(product, upgrade);
            } if (upgrade.getIdcible() == -1){
                updateBonus(null, upgrade);
            }
        }
        world.setMoney(world.getMoney() - upgrade.getSeuil());
        saveWorldToXml(world, username);    
    }

    void updateAngel(String username, PallierType newAngel) throws JAXBException, FileNotFoundException {
        world = getWorld(username);
        PallierType angel = findAngelByName(world, newAngel.getName());
        world.setActiveangels(world.getActiveangels() - angel.getSeuil());
        angel.setUnlocked(true);
        List<ProductType> listProducts = world.getProducts().getProduct();
        for (ProductType product : listProducts){
            if (angel.getIdcible() > 0 && angel.getIdcible()==product.getId()) {
                    updateBonus(product, angel);
            } if (angel.getIdcible() == 0) {
                    updateBonus(product, angel);
            }
        }
        world.setMoney(world.getMoney() - angel.getSeuil());
        saveWorldToXml(world, username);
    }

    public void resetWorld(String username, World world) throws JAXBException, FileNotFoundException {
        double nbAngel = (Math.floor(150 * Math.sqrt(world.getScore() / Math.pow(10, 5)) - world.getTotalangels()));
        double score = world.getScore();
        cont = JAXBContext.newInstance(World.class);
        u = cont.createUnmarshaller();
        InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
        World newWorld = (World) u.unmarshal(input);    
        newWorld.setActiveangels(nbAngel);
        newWorld.setTotalangels(nbAngel);
        newWorld.setScore(score);
        saveWorldToXml(newWorld, username);
    }
    
    private void calScore(World world) {
        double score = world.getScore();
        ProductsType products = world.getProducts();
        List<ProductType> listProducts= products.getProduct();
        for (ProductType product : listProducts) {
            if(!product.isManagerUnlocked()){
                if(product.getTimeleft()>=0 && product.getTimeleft()<(System.currentTimeMillis()- world.getLastupdate())){
                    score = world.getScore() + product.getRevenu();
                }else{
                    product.setTimeleft(product.getTimeleft()-(System.currentTimeMillis()-world.getLastupdate()));
                }                  
            }else{
                double quantite = (double) ((System.currentTimeMillis()- world.getLastupdate()-product.getTimeleft())/product.getVitesse());
                quantite = Math.floor(quantite);
                score = world.getScore() + product.getRevenu()*quantite;
                product.setTimeleft(product.getTimeleft()-(System.currentTimeMillis()-world.getLastupdate()));             
            }
        }
        world.setScore(score);
    }

    private void updateBonus(ProductType product, PallierType pallier) {
       if (pallier.getTyperatio() == TyperatioType.VITESSE) product.setVitesse((int) (product.getVitesse() / pallier.getRatio()));
        else 
            product.setRevenu(product.getRevenu() * pallier.getRatio());
    }



}
