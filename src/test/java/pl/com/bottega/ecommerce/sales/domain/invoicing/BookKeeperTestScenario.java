package pl.com.bottega.ecommerce.sales.domain.invoicing;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BookKeeperTestScenario {

    @Test
    void invoiceRequestWithTwoRequestShouldShouldCallCalculateTaxMethodTwice(){

    }

    @Test
    void invoiceRequestWithOneRequestShouldReturnInvoiceWithOneItem(){
        Tax testTax = new Tax(Money.ZERO, "");
        TaxPolicy mockedPolicy = Mockito.mock(TaxPolicy.class);

        Mockito.doReturn(testTax)
                .when(mockedPolicy)
                .calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));

        Product testProduct = new Product(Id.generate(), Money.ZERO, "ProductMock", ProductType.STANDARD);
        InvoiceFactory mockedFactory = Mockito.mock(InvoiceFactory.class);
        ClientData mockedClientData = Mockito.mock(ClientData.class);

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
