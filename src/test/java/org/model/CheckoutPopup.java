package org.model;

import org.util.Common;
import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CheckoutPopup{
    private final Page page;
    private final FrameLocator checkoutFrame;
    private final Locator optionCreditCard, inputCardNumber, inputCvv, inputExpDate, radioNoPromo, radioPromoFlashSale, radioPromoTesting, labelTotal, buttonPaynow, labelPaymentStatus, labelPaymentTotal, labelPromoEmpty;

    public CheckoutPopup(Page page) {
        this.page = page;
        this.checkoutFrame = page.frameLocator("//iframe[@id='snap-midtrans']");
        this.optionCreditCard = checkoutFrame.locator("[href='#/credit-card']");
        
        // credit card details
        this.inputCardNumber = checkoutFrame.locator(".card-number-input-container .valid-input-value");
        this.inputCvv = checkoutFrame.locator("[id='card-cvv']");
        this.inputExpDate = checkoutFrame.locator("[id='card-expiry']");
        
        // promo radio buttons
        this.radioPromoFlashSale = checkoutFrame.getByText("Promo Flash Sale");
        this.radioPromoTesting = checkoutFrame.getByText("Promo Testing");
        this.radioNoPromo = checkoutFrame.getByText("Proceed without promo");

        this.labelTotal = checkoutFrame.locator("[class='header-amount']");
        this.buttonPaynow = checkoutFrame.getByText("Pay now");

        // labels after success payment
        this.labelPaymentStatus = checkoutFrame.locator("[class^='text-headline']").first();
        this.labelPaymentTotal = checkoutFrame.locator("[class^='text-headline']").nth(1);

        // label when promo quota is full (promo empty)
        this.labelPromoEmpty = checkoutFrame.getByText("Promo quota is fully used");
    }

    public void choosePaymentMethod(){
        assertThat(optionCreditCard).isVisible();
        optionCreditCard.click();
    }

    public int submitPaymentInformation(String cardNumber, String cvv, String expDate, String promo, boolean simulatePromoEmpty){
        final int totalBefore = Common.getRawTotal(labelTotal.innerText());
        inputCardNumber.fill(cardNumber);
        inputExpDate.fill(expDate);
        inputCvv.fill(cvv);
        final int totalAfter = Common.getRawTotal(labelTotal.innerText());

        // promo testing and flash sale: diff should be 1000
        final int totalDiff = totalBefore - totalAfter;

        // choose promo
        if(promo.equalsIgnoreCase("flashsale")){
            radioPromoFlashSale.click();
            if(totalDiff != 1000){
                throw new RuntimeException("total does not correct after applying promo");
            }
        } else if (promo.equalsIgnoreCase("testing")){
            radioPromoTesting.click();
            if(totalDiff != 1000){
                throw new RuntimeException("total does not correct after applying promo");
            }
        } else{
            radioNoPromo.click();
        }
        
        // submit
        buttonPaynow.click();

        // check simulate empty promo popup
        if(simulatePromoEmpty){
            assertThat(labelPromoEmpty).isVisible();
        }

        // return value for later use on bank 3ds popup
        if(promo.equalsIgnoreCase("")){
            return totalBefore;
        } else{
            return totalAfter;
        }
    }

    public void confirmPayment(){
        assertThat(labelPaymentStatus).containsText("Payment successful");
    }

    public FrameLocator getCurrentFrame(){
        return checkoutFrame;
    }
}