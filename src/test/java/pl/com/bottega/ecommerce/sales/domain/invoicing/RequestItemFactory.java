package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.util.Currency;

public class RequestItemFactory {
    private static Product simpleProduct = new Product(Id.generate(), new Money(new BigDecimal(20), Currency.getInstance("EUR")), "TestProduct", ProductType.STANDARD);
    public static RequestItem createSimpleRequestItem(){
        ProductData data = simpleProduct.generateSnapshot();
        int quantity = 2;
        return new RequestItem(data, quantity, data.getPrice().multiplyBy(quantity));
    }
}
