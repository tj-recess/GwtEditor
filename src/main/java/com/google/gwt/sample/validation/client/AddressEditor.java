package com.google.gwt.sample.validation.client;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.sample.validation.shared.Address;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AddressEditor extends Composite implements Editor<Address>, HasEditorErrors<Address> {

    private final Binder BINDER = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, AddressEditor> {
    }

    interface EditorDriver extends SimpleBeanEditorDriver<Address, AddressEditor> {
    }

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private final GreetingServiceAsync RPC = GWT.create(GreetingService.class);

    public AddressEditor() {
        initWidget(BINDER.createAndBindUi(this));
        editorDriver.initialize(this);
        editorDriver.edit(new Address());
    }

    @UiField
    ValueBoxEditorDecorator<String> street;
    @UiField
    ValueBoxEditorDecorator<String> zip;
    @UiField
    Button save;
    @UiField
    ValueBoxEditorDecorator<String> id;
    @UiField
    Button edit;

    public void edit(String id) {
        RPC.getAddress(id, new AsyncCallback<Address>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("RPC get call failed... " + caught.getMessage());
            }

            @Override
            public void onSuccess(Address address) {
                editorDriver.edit(address);
            }
        });
    }

    @UiHandler("edit")
    void onEditClick(ClickEvent event) {
        edit(id.asEditor().getValue());
    }

    @UiHandler("save")
    void onSaveClick(ClickEvent event) {
        Address address = editorDriver.flush();
        RPC.saveAddress(address, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ConstraintViolationException) {
                    Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) caught)
                        .getConstraintViolations();
                    editorDriver.setConstraintViolations(violations);
                    Window.alert(getErrors(violations));
                } else {
                    Window.alert("RPC save call failed... " + caught.getMessage());
                }
            }

            @Override
            public void onSuccess(String addressId) {
                Window.alert("Saved address with ID: " + addressId);
            }
        });
    }

    private String getErrors(Set<ConstraintViolation<?>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void showErrors(List<EditorError> errors) {
        zip.showErrors(errors);
    }
}
