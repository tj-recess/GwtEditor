package com.google.gwt.sample.validation.client;

import javax.ws.rs.Path;

import com.google.gwt.sample.validation.shared.Address;

@Path("/api/v1/addresses")
public interface AddressService extends RestApi<Address> {
	// everything inherited
}
