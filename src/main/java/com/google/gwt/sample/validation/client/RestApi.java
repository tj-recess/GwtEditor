package com.google.gwt.sample.validation.client;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface RestApi<T> extends RestService {

    @GET
    @Path("own")
    public void getForOwner(MethodCallback<T> callback);

    @GET
    @Path("{id}")
    public void get(@PathParam("id") String id, MethodCallback<T> callback);

    @GET
    @Path("all")
    public void listAll(MethodCallback<List<T>> callback);

    @POST
    @Path("save")
    public void save(T obj, MethodCallback<T> callback);

}