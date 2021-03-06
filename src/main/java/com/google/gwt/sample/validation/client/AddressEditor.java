package com.google.gwt.sample.validation.client;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.sample.validation.client.widget.BetterValueBoxEditorDecorator;
import com.google.gwt.sample.validation.shared.Address;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AddressEditor extends Composite implements Editor<Address> {

    private final Binder BINDER = GWT.create(Binder.class);

    interface Binder extends UiBinder<Widget, AddressEditor> {
    }

    interface EditorDriver extends SimpleBeanEditorDriver<Address, AddressEditor> {
    }

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private final GreetingServiceAsync RPC = GWT.create(GreetingService.class);
    private final AddressService addressServiceResty = GWT.create(AddressService.class);

    public AddressEditor() {
        initWidget(BINDER.createAndBindUi(this));
        editorDriver.initialize(this);
        editorDriver.edit(new Address());
    }

    @UiField
    BetterValueBoxEditorDecorator<String> street;
    @UiField
    TextBox zip;
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
        // edit(id.asEditor().getValue());
        editUsingRest(id.asEditor().getValue());
    }

    private void editUsingRest(String id) {
        addressServiceResty.get(id, new MethodCallback<Address>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                Window.alert("RPC get call failed... " + exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Address address) {
                editorDriver.edit(address);
            }
        });
    }

    @UiHandler("save")
    void onSaveClick(ClickEvent event) {
        Address address = editorDriver.flush();
        Set<ConstraintViolation<?>> violations = validateAddressOnClient(address);
        if (!violations.isEmpty()) {
            editorDriver.setConstraintViolations(violations);
        } else {
            // saveUsingRpc(address);
            saveUsingRest(address);
        }
    }

    private void saveUsingRest(Address address) {
        addressServiceResty.save(address, new MethodCallback<Address>() {

            @Override
            public void onFailure(Method method, Throwable exception) {
                Window.alert("REST save call failed... " + exception.getMessage());
            }

            @Override
            public void onSuccess(Method method, Address address) {
                Window.alert("Saved address: " + address);
            }
        });
    }

    private void saveUsingRpc(Address address) {
        RPC.saveAddress(address, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof ConstraintViolationException) {
                    // FIXME Setting constraint violations here is not
                    // displaying errors!
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

    private Set<ConstraintViolation<?>> validateAddressOnClient(Address address) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Address>> violations = validator.validate(address, Default.class);
        Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>();
        for (ConstraintViolation<Address> violation : violations) {
            temp.add(violation);
        }
        return temp;
    }

    private String getErrors(Set<ConstraintViolation<?>> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage());
            sb.append("\n");
        }
        return sb.toString();
    }
}
