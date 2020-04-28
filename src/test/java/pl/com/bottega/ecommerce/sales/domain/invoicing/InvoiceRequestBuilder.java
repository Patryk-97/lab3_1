package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.mockito.Mock;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestBuilder {
	private ClientData clientData = null;
	private List<RequestItem> items = new ArrayList<>();
	
	public InvoiceRequestBuilder add(RequestItem item) {
		this.items.add(item);
		return this;
	}
	
	public InvoiceRequestBuilder setClientData(ClientData clientData) {
		this.clientData = clientData;
		return this;
	}
	
	public InvoiceRequest build() {
		if (this.clientData == null) this.clientData = Mockito.mock(ClientData.class);
		
		InvoiceRequest rV = new InvoiceRequest(this.clientData);
		items.forEach(rV::add);
		return rV;
	}
}
