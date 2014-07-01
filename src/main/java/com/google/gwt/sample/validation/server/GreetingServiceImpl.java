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
package com.google.gwt.sample.validation.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.sample.validation.client.GreetingService;
import com.google.gwt.sample.validation.shared.Address;
import com.google.gwt.sample.validation.shared.Person;
import com.google.gwt.sample.validation.shared.ServerGroup;

import org.hibernate.validator.engine.ValidationSupport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final HashMap<String, Address> addressDb = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(0);

    @Override
    public SafeHtml greetServer(Person person) throws IllegalArgumentException, ConstraintViolationException {
        // Verify that the input is valid.
        Set<ConstraintViolation<Person>> violations = validator.validate(person, Default.class, ServerGroup.class);
        if (!violations.isEmpty()) {
            Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(violations);
            throw new ConstraintViolationException(temp);
        }

        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");

        // Escape data from the client to avoid cross-site script vulnerabilities.
        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        SafeHtml safeHtml = builder//
            .appendEscapedLines("Hello, " + person.getName() + "!")//
            .appendHtmlConstant("<br>")//
            .appendEscaped("I am running " + serverInfo + ".")//
            .appendHtmlConstant("<br><br>")//
            .appendEscaped("It looks like you are using: ")//
            .appendEscaped(userAgent)//
            .toSafeHtml();
        return safeHtml;
    }

    @Override
    public ValidationSupport dummy() {
        return null;
    }

    @Override
    public Address getAddress(String id) throws IllegalArgumentException, ConstraintViolationException {
        return this.addressDb.get(id);
    }

    @Override
    public String saveAddress(Address address) throws IllegalArgumentException, ConstraintViolationException {
        this.validate(address);
        if (address.getId() == null) {
            address.setId(getNextId() + "");
        }
        this.addressDb.put(address.getId() + "", address);
        return address.getId();
    }

    private long getNextId() {
        return nextId.incrementAndGet();
    }

    private <T> void validate(T bean) {
        Set<ConstraintViolation<T>> violations = validator.validate(bean, Default.class, ServerGroup.class);
        if (!violations.isEmpty()) {
            Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(violations);
            throw new ConstraintViolationException(temp);
        }
    }
}