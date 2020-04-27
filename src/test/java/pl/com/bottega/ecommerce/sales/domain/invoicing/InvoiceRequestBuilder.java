package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestBuilder {
    private ClientData clientData = new ClientData(Id.generate(), "name");
    private List<RequestItem> items = new ArrayList<>();

    public InvoiceRequest build(){
        InvoiceRequest request = new InvoiceRequest(clientData);
        items.forEach(request::add);
        return request;
    }

    public InvoiceRequestBuilder withClientData(ClientData clientData){
        this.clientData = clientData;
        return this;
    }

    public InvoiceRequestBuilder withRequestItem(RequestItem item){
        this.items.add(item);
        return this;
    }

    public static InvoiceRequestBuilder invoiceRequest(){
        return new InvoiceRequestBuilder();
    }
}
