package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestBuilder {

    private ClientData clientData = new ClientData(Id.generate(), "Test name");
    private List<RequestItem> invoiceItems = new ArrayList<>();

    public InvoiceRequest build() {
        if (clientData == null)
            throw new NullPointerException();
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        for (RequestItem ri : invoiceItems) {
            invoiceRequest.add(ri);

        }
        return invoiceRequest;
    }

    public InvoiceRequestBuilder clientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public InvoiceRequestBuilder requestItem(RequestItem item) {
        this.invoiceItems.add(item);
        return this;
    }

    public static InvoiceRequestBuilder invoiceRequest() {
        return new InvoiceRequestBuilder();
    }
}
