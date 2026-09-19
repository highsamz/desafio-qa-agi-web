package br.com.samuellima.qa.base;

import br.com.samuellima.qa.config.TestConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ScreenshotExtension.class)
public abstract class BaseTest {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    @BeforeEach
    void setUp() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
                .launch(new LaunchOptions().setHeadless(TestConfig.isHeadless()));
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions().setBaseURL(TestConfig.baseUrl()));
        Page page = context.newPage();

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        CONTEXT.set(context);
        PAGE.set(page);

        page.navigate("/");
    }

    @AfterEach
    void tearDown() {
        closeQuietly(PAGE.get());
        closeQuietly(CONTEXT.get());
        closeQuietly(BROWSER.get());
        closeQuietly(PLAYWRIGHT.get());

        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }

    protected Page page() {
        return PAGE.get();
    }

    private void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Falha ao fechar recurso Playwright: " + e.getMessage());
            }
        }
    }
}