package pl.com.bottega.ecommerce.sales.domain.invoicing;


import org.junit.jupiter.api.BeforeEach;
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
	
	InvoiceRequest request;
	BookKeeper bookKeeper;
	Product sampleProduct;
	RequestItem requestItem;
	Tax tax;
	
	@BeforeEach
	public void prepare() {
		bookKeeper = new BookKeeper(new InvoiceFactory());
		request = new InvoiceRequest(clientDataStub);
		tax = new Tax(Money.ZERO, "");
		
		sampleProduct = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
		requestItem = new RequestItem(sampleProduct.generateSnapshot(), 1, Money.ZERO);
	}
	
	@Test
	void invoiceWithSingleItemRequest_Should_ReturnInvoiceWithSingleItem() {
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
		request.add(requestItem);
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		assertThat(invoice.getItems().size(), is(1));
	}
}