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
package com.google.gwt.sample.validation.client;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.example.client.dispatcher.DispatcherFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Validation implements EntryPoint {

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        // resty-gwt settings
        Defaults.setDateFormat(null);
        Defaults.setServiceRoot(GWT.getHostPageBaseURL());
        Defaults.setDispatcher((new DispatcherFactory()).cachingDispatcher());

        AddressEditor view = new AddressEditor();
        RootPanel.get("view").add(view);
    }
}
