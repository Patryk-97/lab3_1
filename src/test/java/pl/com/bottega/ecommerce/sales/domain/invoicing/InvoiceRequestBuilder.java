package pl.com.bottega.ecommerce.sales.domain.invoicing;

import java.util.ArrayList;
import java.util.List;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;

public class InvoiceRequestBuilder {

    private ClientData clientData = null;

    private List<RequestItem> requestItems = new ArrayList<>();

    public InvoiceRequest build() {
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        requestItems.forEach(invoiceRequest::add);
        return invoiceRequest;
    }

    public InvoiceRequestBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public InvoiceRequestBuilder addRequestItem(RequestItem requestItem) {
        requestItems.add(requestItem);
        return this;
    }
}
