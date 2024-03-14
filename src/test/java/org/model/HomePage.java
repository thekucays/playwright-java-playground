package org.model;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePage{
    private final Page page;
    private final Locator buttonBuyNow, buttonCheckout;

    public HomePage(Page page) {
        this.page = page;
        this.buttonBuyNow = page.getByText("BUY NOW");
        this.buttonCheckout = page.getByText("CHECKOUT");
    }

    public void navigate() {
        page.navigate("https://demo.midtrans.com");
    }

    public void checkoutItem(){
        assertThat(buttonBuyNow).isVisible();
        buttonBuyNow.click();
        assertThat(buttonCheckout).isVisible();
        buttonCheckout.click();
    }
}