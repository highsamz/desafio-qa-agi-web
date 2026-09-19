package br.com.samuellima.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SearchResultsPage {

    private final Page page;

    private final Locator heading;
    private final Locator results;
    private final Locator emptyMessage;
    private final Locator searchField;
    private final Locator searchSubmit;

    public SearchResultsPage(Page page) {
        this.page = page;
        this.heading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setLevel(1));
        this.results = page.locator("a[rel='bookmark']");
        this.emptyMessage = page.getByText("nada foi encontrado");
        this.searchField = page.getByPlaceholder("Digite sua busca")
                .locator("visible=true");
        this.searchSubmit = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Pesquisar"));
    }

    public SearchResultsPage searchByUrl(String term) {
        String encoded = URLEncoder.encode(term, StandardCharsets.UTF_8);
        page.navigate("/?s=" + encoded);
        return this;
    }

    public SearchResultsPage searchByField(String term) {
        searchField.fill(term);
        searchSubmit.click();
        page.waitForLoadState();
        return this;
    }

    public String headingText() {
        return heading.textContent();
    }

    public int resultsCount() {
        return results.count();
    }

    public boolean isEmptyMessageVisible() {
        return emptyMessage.isVisible();
    }

    public List<String> resultTitles() {
        return results.allTextContents();
    }
}