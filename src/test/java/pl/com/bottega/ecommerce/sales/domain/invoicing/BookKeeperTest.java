package pl.com.bottega.ecommerce.sales.domain.invoicing;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {
	@Mock
	ClientData clientDataStub;
	
	@Mock
	TaxPolicy taxPolicyMock;
	
	
	@Test
	void invoiceWithSingleItemRequest_Should_ReturnInvoiceWithSingleItem(){
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		InvoiceRequest request = new InvoiceRequest(clientDataStub);
		var tax = new Tax(Money.ZERO, "");
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
		request.add(new RequestItem(new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD).generateSnapshot(), 1, Money.ZERO));
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		assertThat(invoice.getItems().size(), is(1));
	}
}