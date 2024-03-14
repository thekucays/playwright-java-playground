package org.example;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// models / page object models
import org.model.HomePage;
import org.model.CheckoutPopup;
import org.model.BankPopup;

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
    void checkoutFlashSale(){
        /*
            checkout using Promo Flash Sale
        */
        HomePage homePage = new HomePage(page);
        homePage.navigate();
        homePage.checkoutItem();

        final String cvvNumber = "123";
        final String cardNumber = "4811111111111114";
        final String expDate = "1225";

        CheckoutPopup checkoutPopup = new CheckoutPopup(page);
        checkoutPopup.choosePaymentMethod();
        final int amountToPay = checkoutPopup.submitPaymentInformation(cardNumber, cvvNumber, expDate, "flashsale", false);

        BankPopup bankPopup = new BankPopup(checkoutPopup.getCurrentFrame());
        bankPopup.inputBankOTP(amountToPay, "112233");

        checkoutPopup.confirmPayment();
    }

    @Test
    void checkoutNoPromo(){
        /*
            checkout without using promo
        */
        HomePage homePage = new HomePage(page);
        homePage.navigate();
        homePage.checkoutItem();

        final String cvvNumber = "123";
        final String cardNumber = "4811111111111114";
        final String expDate = "1225";

        CheckoutPopup checkoutPopup = new CheckoutPopup(page);
        checkoutPopup.choosePaymentMethod();
        final int amountToPay = checkoutPopup.submitPaymentInformation(cardNumber, cvvNumber, expDate, "", false);

        BankPopup bankPopup = new BankPopup(checkoutPopup.getCurrentFrame());
        bankPopup.inputBankOTP(amountToPay, "112233");

        checkoutPopup.confirmPayment();
    }

    @Test
    void checkoutPromoEmpty(){
        /*
            checkout using Promo Testing
            - promo quota is empty
            - so expecting to see popup telling that promo empty
        */
        HomePage homePage = new HomePage(page);
        homePage.navigate();
        homePage.checkoutItem();

        final String cvvNumber = "123";
        final String cardNumber = "4811111111111114";
        final String expDate = "1225";

        CheckoutPopup checkoutPopup = new CheckoutPopup(page);
        checkoutPopup.choosePaymentMethod();
        checkoutPopup.submitPaymentInformation(cardNumber, cvvNumber, expDate, "testing", true);
    }
}
