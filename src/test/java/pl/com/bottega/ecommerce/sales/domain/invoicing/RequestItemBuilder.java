package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

public class RequestItemBuilder
{
    private ProductData productData = ProductBuilder.builder().build().generateSnapshot();
    private int quantity = 1;
    private Money totalCost = new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY);

    public RequestItem build()
    {
        return new RequestItem(this.productData, this.quantity, this.totalCost);
    }

    public RequestItemBuilder withProductData(ProductData productData)
    {
        this.productData = productData;
        return this;
    }

    public RequestItemBuilder withQuantity(int quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public RequestItemBuilder withTotalCost(Money totalCost)
    {
        this.totalCost = totalCost;
        return this;
    }

    public static RequestItemBuilder builder()
    {
        return new RequestItemBuilder();
    }
}
