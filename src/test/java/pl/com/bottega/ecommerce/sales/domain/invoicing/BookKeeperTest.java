package pl.com.bottega.ecommerce.sales.domain.invoicing;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
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
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		request.add(requestItem);
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		assertThat(invoice.getItems().size(), is(1));
	}
	
	@Test
	void createInvoiceWith2Item_Should_CallCalculateTax2Times() {
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		request.add(requestItem);
		request.add(requestItem);
		bookKeeper.issuance(request, taxPolicyMock);
		Mockito.verify(taxPolicyMock, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
	}
	
	@Test
	void createEmptyInvoice_Should_ReturnEmptyInvoice() {
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		assertThat(invoice.getItems().size(), is(0));
	}
	
	@Test
	void createEmptyInvoice_Should_NotCallCalculateTax() {
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		Mockito.verify(taxPolicyMock, Mockito.times(0)).calculateTax(Mockito.any(), Mockito.any());
	}
	
	@Test
	void passNullAsInvoiceRequestToIssuance_Should_ThrowNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () -> bookKeeper.issuance(null, taxPolicyMock));
	}
	
	@Test
	void ifInvoiceFactoryIsUsedInIssuanceMethodCall() {
		var factoryMock = Mockito.mock(InvoiceFactory.class);
		BookKeeper keeper = new BookKeeper(factoryMock);
		keeper.issuance(request, taxPolicyMock);
		
		Mockito.verify(factoryMock, Mockito.atLeastOnce()).create(Mockito.any());
	}
	
	@Test
	void createInvoiceWithManyItems_Should_ReturnInvoiceWithTheSameNumberOfItems() {
		int numberOfItemsInInvoice = 10;
		for(int i = 0; i < numberOfItemsInInvoice; ++i)
			request.add(requestItem);
		
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);
		assertThat(invoice.getItems().size(), is(numberOfItemsInInvoice));
	}
}
