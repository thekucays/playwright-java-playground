package org.example;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class App {
    // Shared between all tests in this class.
    static Playwright playwright;
    static Browser browser;

    // New instance for each test method.
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void checkoutWithPromo(){
        page.navigate("https://demo.midtrans.com");

        // locators
        Locator buttonBuyNow = page.getByText("BUY NOW");
        Locator buttonCheckout = page.getByText("CHECKOUT");


        // start checkout
        assertThat(buttonBuyNow).isVisible();
        buttonBuyNow.click();
        assertThat(buttonCheckout).isVisible();
        buttonCheckout.click();

        // checkout popup, inside an iframe ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        FrameLocator checkoutFrame = page.frameLocator("//iframe[@id='snap-midtrans']");
        Locator optionCreditCard = checkoutFrame.locator("[href='#/credit-card']");
        assertThat(optionCreditCard).isVisible();
        optionCreditCard.click();

        final String cvvNumber = "123";
        final String cardNumber = "4811111111111114";
        final String expDate = "1225";

        Locator inputCardNumber = checkoutFrame.locator(".card-number-input-container .valid-input-value");
        Locator inputExpDate = checkoutFrame.locator("[id='card-expiry']");
        Locator inputCvv = checkoutFrame.locator("[id='card-cvv']");
        Locator radioPromoFlashSale = checkoutFrame.getByText("Promo Flash Sale");
        Locator radioPromoTesting = checkoutFrame.getByText("Promo Testing");
        Locator radioNoPromo = checkoutFrame.getByText("Proceed without promo");
        Locator labelTotal = checkoutFrame.locator("[class='header-amount']");

        // choosing promo and count total
        radioNoPromo.click();
        final int totalBefore = Common.getRawTotal(labelTotal.innerText());
        inputCardNumber.fill(cardNumber);
        inputExpDate.fill(expDate);
        inputCvv.fill(cvvNumber);
        final int totalAfter = Common.getRawTotal(labelTotal.innerText());

        // promo testing and flash sale: diff should be 1000
        final int totalDiff = totalBefore - totalAfter;
        System.out.println(">>> total diff: " + totalDiff);


        Locator buttonPaynow = checkoutFrame.getByText("Pay now");
        buttonPaynow.click();
        // end of checkout popup //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // 3ds (bank issue), inside an iframe ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        FrameLocator bankFrame = checkoutFrame.frameLocator("//iframe[@class='iframe-3ds']");
        
        final String otp = "112233";
        Locator inputOtp = bankFrame.locator("[id='otp']");
        Locator buttonOK = bankFrame.locator("[type='submit']");
        assertThat(inputOtp).isVisible();
        inputOtp.fill(otp);

        assertThat(buttonOK).isVisible();
        buttonOK.click();
        // end of 3ds popup //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // back to snap-midtrans for successful payment 
        Locator labelPaymentStatus = checkoutFrame.locator("[class^='text-headline']").first();
        Locator labelPaymentTotal = checkoutFrame.locator("[class^='text-headline']").nth(1);
        assertThat(labelPaymentStatus).containsText("Payment successful");
        System.out.println(">>> payment total: " + labelPaymentTotal.innerText());
        // end of snap-midtrans //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
}
