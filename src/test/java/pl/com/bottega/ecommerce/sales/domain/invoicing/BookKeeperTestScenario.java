package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class BookKeeperTestScenario {

    private Tax testTax;
    private TaxPolicy mockedPolicy;
    private InvoiceFactory mockedFactory;
    private ClientData mockedClientData;

    @BeforeEach
    void init() {
        testTax = new Tax(Money.ZERO, "Test tax");
        mockedPolicy = Mockito.mock(TaxPolicy.class);
        mockedFactory = Mockito.mock(InvoiceFactory.class);
        mockedClientData = Mockito.mock(ClientData.class);
    }

    @Test
    void invoiceRequestWithTwoItemsShouldCallCalculateTaxMethodTwice() {
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        Product testProduct1 = new Product(Id.generate(), Money.ZERO, "ProductMock1", ProductType.STANDARD);
        Product testProduct2 = new Product(Id.generate(), Money.ZERO, "ProductMock2", ProductType.STANDARD);

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));
        InvoiceRequest request = new InvoiceRequest(mockedClientData);
        request.add(new RequestItem(testProduct1.generateSnapshot(), 1, Money.ZERO));
        request.add(new RequestItem(testProduct2.generateSnapshot(), 1, Money.ZERO));
        BookKeeper keeper = new BookKeeper(mockedFactory);

        Invoice result = keeper.issuance(request, mockedPolicy);
        
        assertThat(result, notNullValue());
        Mockito.verify(mockedPolicy,Mockito.times(2)).calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
    }

    @Test
    void invoiceRequestWithOneRequestShouldReturnInvoiceWithOneItem() {
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        Product testProduct = new Product(Id.generate(), Money.ZERO, "ProductMock", ProductType.STANDARD);

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = new InvoiceRequest(mockedClientData);
        request.add(new RequestItem(testProduct.generateSnapshot(), 1, Money.ZERO));

        BookKeeper keeper = new BookKeeper(mockedFactory);

        int result = keeper.issuance(request, mockedPolicy).getItems().size();

        assertThat(result, is(1));
    }

}
