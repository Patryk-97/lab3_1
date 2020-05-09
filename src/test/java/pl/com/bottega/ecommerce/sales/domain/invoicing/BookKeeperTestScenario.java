package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = InvoiceRequestFactory.builder()
                .attachClientData(mockedClientData)
                .addItem()
                .ofProductName("TestProduct1")
                .ofQuantity(1)
                .accept()
                .addItem()
                .ofProductName("TestProduct2")
                .ofQuantity(1)
                .accept()
                .build();
        BookKeeper keeper = new BookKeeper(mockedFactory);

        keeper.issuance(request, mockedPolicy);

        Mockito.verify(mockedPolicy, Mockito.times(2)).calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
    }

    @Test
    void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));


        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = InvoiceRequestFactory.builder()
                .attachClientData(mockedClientData)
                .addItem()
                .ofProductName("ProductMock")
                .ofQuantity(1)
                .accept()
                .build();

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

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        var request = InvoiceRequestFactory.builder()
                .attachClientData(mockedClientData)
                .addItem()
                .ofProductName(name)
                .ofQuantity(1)
                .ofId(testId)
                .accept()
                .build();

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

        Mockito.doAnswer((Answer<Invoice>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return new Invoice(Id.generate(), (ClientData) args[0]);
        }).when(mockedFactory).create(Mockito.any(ClientData.class));

        var builder = InvoiceRequestFactory.builder();

        InvoiceRequest firstRequest = builder
                .attachClientData(firstTestClient)
                .addItem()
                .ofQuantity(1)
                .ofProductName(name)
                .accept()
                .build();

        InvoiceRequest secondRequest = builder
                .attachClientData(secondTestClient)
                .build();

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

    @Test
    void invoiceRequestThatHaveNotAnyItemsShouldNotCallCalculateTax() {
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        Mockito.doReturn(new Invoice(Id.generate(), mockedClientData))
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = InvoiceRequestFactory.builder().attachClientData(mockedClientData).build();

        BookKeeper keeper = new BookKeeper(mockedFactory);

        keeper.issuance(request, mockedPolicy);

        Mockito.verify(mockedPolicy, Mockito.times(0)).calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
    }

    @Test
    void invoiceRequestShouldCallCreateMethodOfInvoiceFactoryAndThrowNPEIfBasicInvoiceIsNull() {
        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
        Mockito.doReturn(null)
                .when(mockedFactory)
                .create(Mockito.any(ClientData.class));

        InvoiceRequest request = InvoiceRequestFactory.builder()
                .attachClientData(mockedClientData)
                .addItem()
                .ofQuantity(1)
                .accept()
                .build();

        BookKeeper keeper = new BookKeeper(mockedFactory);

        assertThrows(NullPointerException.class, () -> keeper.issuance(request, mockedPolicy));
        Mockito.verify(mockedFactory, Mockito.atLeast(1)).create(Mockito.any(ClientData.class));
    }
}
