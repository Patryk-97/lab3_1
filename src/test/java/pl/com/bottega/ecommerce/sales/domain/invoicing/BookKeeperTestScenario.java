package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.Matchers.not;

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
        mockedClientData = Mockito.mock(Client.class).generateSnapshot();
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

        keeper.issuance(request, mockedPolicy);

        Mockito.verify(mockedPolicy, Mockito.times(2)).calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
    }

    @Test
    void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {
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

    @Test
    void invoiceRequestWithPreparedItemShouldReturnInvoiceWithItemThatHaveUnmodifiedDataAsProvidedAsInput() {
        String name = randomAlphabetic(8);
        Id testId = Id.generate();
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        Product testProduct = new Product(testId, Money.ZERO, name, ProductType.STANDARD);

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = new InvoiceRequest(mockedClientData);
        request.add(new RequestItem(testProduct.generateSnapshot(), 1, Money.ZERO));

        BookKeeper keeper = new BookKeeper(mockedFactory);

        var product = keeper.issuance(request, mockedPolicy).getItems().get(0).getProduct();
        var resultName = product.getName();
        var resultPrice = product.getPrice();
        var resultType = product.getType();
        var resultId = product.getProductId();

        assertThat(resultName, is(name));
        assertThat(resultPrice, is(Money.ZERO));
        assertThat(resultType, is(ProductType.STANDARD));
        assertThat(resultId, is(testId));
    }

    @Test
    void twoInvoiceRequestsWithTheSameItemsFromTwoOtherClientsShouldReturnTwoInvoicesThatShouldHaveDifferenceInClientDataOnly() {
        ClientData firstTestClient = new Client().generateSnapshot();
        ClientData secondTestClient = new Client().generateSnapshot();
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        String name = randomAlphabetic(13);
        Product testProduct = new Product(Id.generate(), Money.ZERO, name, ProductType.STANDARD);

        Mockito.doAnswer((Answer<Invoice>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return new Invoice(Id.generate(), (ClientData) args[0]);
        }).when(mockedFactory).create(Mockito.any(ClientData.class));

        InvoiceRequest firstRequest = new InvoiceRequest(firstTestClient);
        firstRequest.add(new RequestItem(testProduct.generateSnapshot(), 1, Money.ZERO));

        InvoiceRequest secondRequest = new InvoiceRequest(secondTestClient);
        secondRequest.add(new RequestItem(testProduct.generateSnapshot(), 1, Money.ZERO));
        BookKeeper keeper = new BookKeeper(mockedFactory);

        var firstInvoice = keeper.issuance(firstRequest, mockedPolicy);
        var secondInvoice = keeper.issuance(secondRequest, mockedPolicy);
        var productFromFirstInvoice = firstInvoice.getItems().get(0).getProduct();
        var productFromSecondInvoice = secondInvoice.getItems().get(0).getProduct();
        var firstCustomer = firstInvoice.getClient();
        var secondCustomer = secondInvoice.getClient();

        assertThat(productFromFirstInvoice, is(productFromSecondInvoice));
        assertThat(firstCustomer, is(firstTestClient));
        assertThat(secondCustomer, is(secondTestClient));
        assertThat(firstCustomer, not(secondCustomer));
    }
}
