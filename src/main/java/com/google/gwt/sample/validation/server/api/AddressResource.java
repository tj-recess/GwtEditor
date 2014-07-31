package com.google.gwt.sample.validation.server.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gwt.sample.validation.shared.Address;

@Path("/v1/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AddressResource {

    private final HashMap<String, Address> addressDb = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(0);

    public AddressResource() {
        addressDb.put("1", new Address("1", "101 Ave 1", "98105"));
        addressDb.put("2", new Address("2", "202 St 2", "32608"));
    }

    // example : http://127.0.0.1:8888/api/v1/addresses/all
    @GET
    @Path("/all")
    public Collection<Address> listAll() {
        return addressDb.values();
    }

    // example : http://127.0.0.1:8888/api/v1/addresses/1
    @GET
    @Path("{id}")
    public Address get(@PathParam("id") String id) {
        return addressDb.get(id);
    }

    @POST
    @Path("save")
    public Address save(Address address) {
        if (address.getId() == null) {
            address.setId(nextId.incrementAndGet() + "");
        }
        addressDb.put(address.getId(), address);
        return address;
    }
}
