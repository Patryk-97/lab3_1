package pl.com.bottega.ecommerce.sales.domain.invoicing.testHelpers;

public class InvoiceRequestFactory {

    private static class InvoiceRequestBuilder{
        private static class RequestItemBuilder{

            public RequestItemBuilder(InvoiceRequestBuilder parentBuilder) {
                
            }
        }

        public RequestItemBuilder addItem(){
            return new RequestItemBuilder(this);
        }
    }

    public static InvoiceRequestBuilder builder(){
        return new InvoiceRequestBuilder();
    }
}
