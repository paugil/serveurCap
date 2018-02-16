/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serveurcap;

import com.google.gson.Gson;
import generated.World;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

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
     * @return an instance of java.lang.String
     */
    @GET
    @Path("world")
    @Produces(MediaType.APPLICATION_XML)
    public World getWorld() {
        return services.readWorldFromXml();
    }
    
    @GET
    @Path("worldJSON")
    @Produces(MediaType.APPLICATION_JSON)
    public String getWorldJSON() {
        World world = services.readWorldFromXml(); 
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
}
