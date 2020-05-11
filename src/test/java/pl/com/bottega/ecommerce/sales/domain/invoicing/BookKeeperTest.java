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
	TaxPolicy taxPolicyMock;
	
	BookKeeper bookKeeper;
	Tax tax;
	
	@BeforeEach
	public void prepare() {
		bookKeeper = new BookKeeper(new InvoiceFactory());
		tax = new Tax(Money.ZERO, "");
	}
	
	@Test
	void invoiceWithSingleItemRequest_Should_ReturnInvoiceWithSingleItem() {
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		Invoice invoice = bookKeeper.issuance(new InvoiceRequestBuilder()
						.add(RequestItemFactory.getRequestItem())
						.build(),
				taxPolicyMock);
		assertThat(invoice.getItems().size(), is(1));
	}
	
	@Test
	void createInvoiceWith2Item_Should_CallCalculateTax2Times() {
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		bookKeeper.issuance(new InvoiceRequestBuilder()
				.add(RequestItemFactory.getRequestItem())
				.add(RequestItemFactory.getRequestItem())
				.build(), taxPolicyMock);
		
		Mockito.verify(taxPolicyMock, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
	}
	
	@Test
	void createEmptyInvoice_Should_ReturnEmptyInvoice() {
		Invoice invoice = bookKeeper.issuance(new InvoiceRequestBuilder().build(), taxPolicyMock);
		assertThat(invoice.getItems().size(), is(0));
	}
	
	@Test
	void createEmptyInvoice_Should_NotCallCalculateTax() {
		Invoice invoice = bookKeeper.issuance(new InvoiceRequestBuilder().build(), taxPolicyMock);
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
		keeper.issuance(new InvoiceRequestBuilder().build(), taxPolicyMock);
		
		Mockito.verify(factoryMock, Mockito.atLeastOnce()).create(Mockito.any());
	}
	
	@Test
	void createInvoiceWithManyItems_Should_ReturnInvoiceWithTheSameNumberOfItems() {
		int numberOfItemsInInvoice = 10;
		var invoiceRequestBuilder = new InvoiceRequestBuilder();
		for (int i = 0; i < numberOfItemsInInvoice; ++i)
			invoiceRequestBuilder.add(RequestItemFactory.getRequestItem());
		
		
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
		Invoice invoice = bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicyMock);
		assertThat(invoice.getItems().size(), is(numberOfItemsInInvoice));
	}
}
