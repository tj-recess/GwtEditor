/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.sample.validation.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import javax.validation.constraints.NotNull;

/**
 * Sample address for validation
 */
public class Address implements IsSerializable {

    /**
     * For serialization.
     */
    public Address() {
    }

    public Address(String id, String street, String zip) {
        this.id = id;
        this.street = street;
        this.zip = zip;
    }

    @NotNull(message = "Invalid Street.")
    private String street;

    @Zip(message = "Invalid Zip Code.")
    private String zip;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        return "Address [street=" + street + ", zip=" + zip + ", id=" + id + "]";
    }

}
