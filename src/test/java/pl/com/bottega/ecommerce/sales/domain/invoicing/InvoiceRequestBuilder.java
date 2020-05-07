package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;

import static org.mockito.Mockito.mock;

public class InvoiceRequestBuilder {

    private InvoiceRequest invoiceRequest = new InvoiceRequest(mock(ClientData.class));

    public InvoiceRequestBuilder withItems(int size, RequestItem requestItem) {
        for (int i = 0; i < size; i++) {
            invoiceRequest.add(requestItem);
        }

        return this;
    }

    public InvoiceRequest build() {
        return invoiceRequest;
    }
}
