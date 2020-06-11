package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {

    private Id id = null;

    private Money price = null;

    private String name = "";

    private ProductType productType = null;

    public Product build() {
        return new Product(id, price, name, productType);
    }

    public ProductBuilder withId(Id aggregateId) {
        id = aggregateId;
        return this;
    }

    public ProductBuilder withPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }
}
