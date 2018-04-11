/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serveurcap;

import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

/**
 * REST Web Service
 *
 * @author Pauline
 */
@Path("generic")
public class GenericResource {

    @Context
    private UriInfo context;
    
    public Services services;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
        services = new Services();
    }

    /**
     * Retrieves representation of an instance of com.mycompany.serveurcap.GenericResource
     * @param request
     * @return an instance of java.lang.String
     */
    @GET
    @Path("world")
    @Produces(MediaType.APPLICATION_XML)
    public World getWorld(@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        String username = request.getHeader("X-User");
        World world = services.readWorldFromXml(username);
        services.saveWorldToXml(world, username);
        return world;
    }
    
    @GET
    @Path("world")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWorldJSON(@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        String username = request.getHeader("X-User");
        World world = services.readWorldFromXml(username); 
        services.saveWorldToXml(world, username);
        return new Gson().toJson(world);
    }

    /**
     * PUT method for updating or cr
     * eating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    @PUT
    @Path("product")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putProduct(@Context HttpServletRequest request, String data) throws JAXBException, FileNotFoundException, InterruptedException{
        ProductType product = new Gson().fromJson(data, ProductType.class);
        String username = request.getHeader("X-User");
        services.updateProduct(username, product);
    }
    
    @PUT
    @Path("manager")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putManager(@Context HttpServletRequest request, String data) throws JAXBException, FileNotFoundException{
        PallierType manager = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-User");
        services.updateManager(username, manager);
    }
    
    @PUT
    @Path("upgrade")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putUpgrade(@Context HttpServletRequest request,String data) throws JAXBException, FileNotFoundException{
        PallierType pallier = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-User");
        services.updateUpgrade(username, pallier);
    }
    
    @PUT
    @Path("angel")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putAngel(@Context HttpServletRequest request, String data) throws JAXBException, FileNotFoundException{
        PallierType pallier = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-User");
        services.updateAngel(username, pallier);
    }
    
    @DELETE
    @Path("world")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWorld(@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        String username = request.getHeader("X-User");
        World world = services.readWorldFromXml(username);
        services.resetWorld(username);
    }  

}
