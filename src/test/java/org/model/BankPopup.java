package org.model;

import org.util.Common;
import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BankPopup{
    private final FrameLocator checkoutFrame, bankFrame;
    private final Locator inputOtp, buttonOK, labelAmount;

    public BankPopup(FrameLocator parentFrame) {
        this.checkoutFrame = parentFrame;
        this.bankFrame = checkoutFrame.frameLocator("//iframe[@class='iframe-3ds']");

        this.inputOtp = bankFrame.locator("[id='otp']");
        this.buttonOK = bankFrame.locator("[type='submit']");
        this.labelAmount = bankFrame.locator("[id='txn_amount']");
    }

    public void inputBankOTP(int amountToPay, String otp){
        // loooping wait for otp page
        int timeoutInSeconds = 8;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutInSeconds * 1000) {
            if (inputOtp.first().isVisible())
                break;
            try{
                Thread.sleep(1000); // Sleep for 1 second before checking again
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        inputOtp.fill(otp);

        final int amountOnScreen = Integer.parseInt(labelAmount.innerText().replaceAll("\\.00$", ""));
        if(amountOnScreen != amountToPay){
            throw new RuntimeException("amount is not correct");
        }

        assertThat(buttonOK).isVisible();
        buttonOK.click();
    }
}