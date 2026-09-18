package br.com.samuellima.qa.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ScreenshotExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }

        Object testInstance = context.getTestInstance().orElse(null);
        if (!(testInstance instanceof BaseTest baseTest)) {
            return;
        }

        Page page = baseTest.page();
        if (page == null) {
            return;
        }

        byte[] screenshot = page.screenshot();
        Allure.getLifecycle().addAttachment(
                "Screenshot da falha", "image/png", "png", screenshot);
    }
}