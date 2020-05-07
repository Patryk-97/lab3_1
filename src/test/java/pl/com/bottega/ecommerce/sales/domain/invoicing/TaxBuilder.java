package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

public class TaxBuilder {

    private Money money = Money.ZERO;
    private String description = "";

    public TaxBuilder withMoney(int denomination) {
        money = new Money(BigDecimal.valueOf(denomination));
        return this;
    }

    public Tax build() {
        return new Tax(money, description);
    }
}
