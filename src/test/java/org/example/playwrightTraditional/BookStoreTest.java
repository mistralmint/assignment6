package org.example.playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Paths;
import java.util.regex.Pattern;

public class BookStoreTest {
    @Test
    public void runBookstoreTest() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                            .setHeadless(false));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(Paths.get("videos/"))
                    .setRecordVideoSize(1280, 720));
            Page page = context.newPage();
            page.navigate("https://depaul.bncollege.com/");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("earbuds");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).press("Enter");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
            page.getByText("brand JBL").first().click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
            page.getByText("Color Black").click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
            page.getByText("Price Over $").click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))).isVisible();
            assertThat(page.locator("div.sku:visible")).isVisible();
            //assertThat(page.locator("div.sku:visible")).isVisible();
            assertThat(page.locator("text=$")).isVisible();
            assertThat(page.locator(".description")).isVisible();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart")).click();
            assertThat(page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName(Pattern.compile("Cart\\s*1\\s*items?", Pattern.CASE_INSENSITIVE)))).isVisible();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();

            assertThat(
                    page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)")).first()).isVisible();
            assertThat(page.locator("text=JBL Quantum True Wireless")).isVisible();
            assertThat(page.getByText("qty:")).isVisible();
            assertThat(page.getByRole(AriaRole.TEXTBOX,
                    new Page.GetByRoleOptions().setName(Pattern.compile("Quantity, edit and press", Pattern.CASE_INSENSITIVE)))).isVisible();
            assertThat(page.locator("#updateCartForm0")).isVisible();
            assertThat(page.locator(".bned-cart-entry-totals-wp")).isVisible();
            assertThat(page.getByText("$164.98").first()).isVisible();
            page.getByText("FAST In-Store PickupDePaul").click();
            Locator sidebar = page.locator(".bned-cart-order-summary-wp");
            sidebar.scrollIntoViewIfNeeded();
            assertThat(sidebar.getByText("$164.98", new Locator.GetByTextOptions().setExact(true))).isVisible();
            assertThat(sidebar.getByText("$3.00", new Locator.GetByTextOptions().setExact(true))).isVisible();
            assertThat(sidebar.getByText("TBD", new Locator.GetByTextOptions().setExact(true))).isVisible();
            assertThat(sidebar.getByText("$167.98", new Locator.GetByTextOptions().setExact(true))).isVisible();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Promo Code")).fill("TEST");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply Promo Code")).click();
            assertThat(page.locator("text=invalid").or(page.locator("text=not valid")).or(page.locator("text=cannot be applied"))).isVisible();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed To Checkout")).first().click();

            assertThat(page.locator("h2:has-text('Create Account')")).isVisible();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();

            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).fill("Marlon");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name (required)")).press("Tab");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).fill("Brando");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name (required)")).press("Tab");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).fill("nicoinsist@gmail.com");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address (required)")).press("Tab");
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("United States: +")).press("Tab");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone Number (required)")).fill("8476791415");
            Locator checkoutSidebar = page.locator(".bned-order-summary-container:visible").first();
            Locator subtotal = checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Order Subtotal') .value");
            assertThat(subtotal).hasText("$164.98");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value")).hasText("$3.00");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value")).hasText("TBD");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp.bned-total .value")).hasText("$167.98");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

            assertThat(page.getByText("Marlon")).isVisible();
            assertThat(page.getByText("Brando")).isVisible();
            assertThat(page.locator("text=nicoinsist@gmail.com")).isVisible();
            assertThat(page.locator("text=847")).isVisible();
            Locator campusLabel = page.locator("#checkoutOrderDetails .bned-entries-delivery-multicampus-name");
            assertThat(campusLabel).hasText("DePaul University Loop Campus & SAIC");
            assertThat(page.locator("text=I'll pick them up").or(page.locator("text=I’ll pick them up"))).isVisible();
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Order Subtotal') .value")).hasText("$164.98");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value")).hasText("$3.00");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value")).hasText("TBD");
            assertThat(checkoutSidebar.locator(".bned-checkout-total-wp.bned-total .value")).hasText("$167.98");
            Locator productLink = page.locator(".bned-order-summary-container .bned-order-summary-entry-name a:visible").filter(new Locator.FilterOptions().setHasText("JBL Quantum True Wireless"));
            assertThat(productLink).isVisible();
            Locator subtotalVisible = checkoutSidebar.locator(".bned-order-summary-entry-total:visible");
            assertThat(subtotalVisible.filter(new Locator.FilterOptions().setHasText("$164.98")).first()).isVisible();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();

            assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Payment Information"))).isVisible();
            Locator paymentSidebar = page.locator(".bned-payment-order-summary, .bned-order-summary-container:visible").first();
            assertThat(paymentSidebar.locator(".bned-order-summary-entry-total:has-text('$164.98')")).isVisible();
            assertThat(paymentSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value")).hasText("$3.00");
            assertThat(paymentSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value")).hasText("$17.22");
            assertThat(paymentSidebar.locator(".bned-checkout-total-wp.bned-total .value")).hasText("$185.20");
            Locator productLinkPayment = page
                    .locator(".bned-order-summary-container .bned-order-summary-entry-name a:visible")
                    .filter(new Locator.FilterOptions().setHasText("JBL Quantum True Wireless"));
            assertThat(productLinkPayment).isVisible();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove product JBL Quantum")).click();
            assertThat(page.locator("body")).containsText(Pattern.compile("(cart\\s*(is\\s*)?empty|no items)", Pattern.CASE_INSENSITIVE));
            page.close();
        }
    }
}
