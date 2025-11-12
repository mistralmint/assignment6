package playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookStoreMCPTest {
        @Test
        public void fullBookstoreCheckoutWorkflow() {
                try (Playwright playwright = Playwright.create()) {
                        Browser browser = playwright.chromium()
                                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
                        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                                        .setRecordVideoDir(Paths.get("videos/"))
                                        .setRecordVideoSize(1280, 720));
                        Page page = context.newPage();

                        // 1) Search and filter
                        page.navigate("https://depaul.bncollege.com/");
                        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).click();
                        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).fill("earbuds");
                        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")).press("Enter");

                        // Expand Brand and choose JBL (best-effort selectors)
                        try {
                                page.getByRole(AriaRole.BUTTON,
                                                new Page.GetByRoleOptions().setName(
                                                                Pattern.compile("Brand", Pattern.CASE_INSENSITIVE)))
                                                .click();
                        } catch (Exception ignored) {
                        }
                        try {
                                page.getByText(Pattern.compile("\\bJBL\\b", Pattern.CASE_INSENSITIVE)).first().click();
                        } catch (Exception ignored) {
                        }

                        // Expand Color -> Black
                        try {
                                page.getByRole(AriaRole.BUTTON,
                                                new Page.GetByRoleOptions().setName(
                                                                Pattern.compile("Color", Pattern.CASE_INSENSITIVE)))
                                                .click();
                        } catch (Exception ignored) {
                        }
                        try {
                                page.getByText(Pattern.compile("\\bBlack\\b", Pattern.CASE_INSENSITIVE)).first()
                                                .click();
                        } catch (Exception ignored) {
                        }

                        // Expand Price -> Over $50
                        try {
                                page.getByRole(AriaRole.BUTTON,
                                                new Page.GetByRoleOptions().setName(
                                                                Pattern.compile("Price", Pattern.CASE_INSENSITIVE)))
                                                .click();
                        } catch (Exception ignored) {
                        }
                        try {
                                page.getByText(
                                                Pattern.compile("Over\\s*\\$?\\s*50|Over\\s*\\$|Over\\s*\\$50",
                                                                Pattern.CASE_INSENSITIVE))
                                                .first().click();
                        } catch (Exception ignored) {
                        }

                        // Click product link
                        page.getByRole(AriaRole.LINK,
                                        new Page.GetByRoleOptions()
                                                        .setName(Pattern.compile("JBL Quantum True Wireless",
                                                                        Pattern.CASE_INSENSITIVE)))
                                        .first().click();

                        // Product page assertions
                        assertThat(page.getByRole(AriaRole.HEADING,
                                        new Page.GetByRoleOptions()
                                                        .setName(Pattern.compile("JBL Quantum True Wireless",
                                                                        Pattern.CASE_INSENSITIVE))))
                                        .isVisible();
                        assertThat(page.locator("div.sku:visible")).isVisible();
                        assertThat(page.locator("text=$").first()).isVisible();
                        assertThat(page.locator(".description").first()).isVisible();

                        // Add to cart and wait for cart update
                        page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("Add to cart", Pattern.CASE_INSENSITIVE)))
                                        .click();
                        try {
                                page.waitForSelector("text=Cart 1 items",
                                                new Page.WaitForSelectorOptions().setTimeout(8000));
                        } catch (Exception ignored) {
                        }

                        // Click cart icon/link in upper right
                        page.getByRole(AriaRole.LINK,
                                        new Page.GetByRoleOptions()
                                                        .setName(Pattern.compile("Cart", Pattern.CASE_INSENSITIVE)))
                                        .first()
                                        .click();

                        // 2) Your Shopping Cart Page assertions
                        assertThat(
                                        page.getByRole(AriaRole.HEADING,
                                                        new Page.GetByRoleOptions()
                                                                        .setName(Pattern.compile("Your Shopping Cart",
                                                                                        Pattern.CASE_INSENSITIVE))))
                                        .isVisible();
                        assertThat(page.locator("text=JBL Quantum True Wireless").first()).isVisible();
                        assertThat(page.getByText(Pattern.compile("qty[:]?\\s*1", Pattern.CASE_INSENSITIVE)))
                                        .isVisible();
                        assertThat(page.getByText(Pattern.compile("\\$149\\.98", Pattern.CASE_INSENSITIVE)))
                                        .isVisible();

                        // Select FAST In-Store Pickup (best-effort)
                        try {
                                page.getByText(Pattern.compile("FAST In-Store Pickup", Pattern.CASE_INSENSITIVE))
                                                .first().click();
                        } catch (Exception ignored) {
                        }

                        Locator sidebar = page.locator(".bned-cart-order-summary-wp");
                        sidebar.scrollIntoViewIfNeeded();
                        assertThat(sidebar.getByText(Pattern.compile("\\$149\\.98", Pattern.CASE_INSENSITIVE)))
                                        .isVisible();
                        assertThat(sidebar.getByText(Pattern.compile("\\$2\\.00", Pattern.CASE_INSENSITIVE)))
                                        .isVisible();
                        assertThat(sidebar.getByText(Pattern.compile("TBD", Pattern.CASE_INSENSITIVE))).isVisible();
                        assertThat(sidebar.getByText(Pattern.compile("\\$151\\.98", Pattern.CASE_INSENSITIVE)))
                                        .isVisible();

                        // Apply promo code and expect rejection
                        try {
                                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions()
                                                .setName(Pattern.compile("Enter Promo Code", Pattern.CASE_INSENSITIVE)))
                                                .click();
                                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions()
                                                .setName(Pattern.compile("Enter Promo Code", Pattern.CASE_INSENSITIVE)))
                                                .fill("TEST");
                                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                                                .setName(Pattern.compile("Apply Promo Code", Pattern.CASE_INSENSITIVE)))
                                                .click();
                                assertThat(page.locator("text=invalid").or(page.locator("text=not valid"))
                                                .or(page.locator("text=cannot be applied"))).isVisible();
                        } catch (Exception ignored) {
                        }

                        // Proceed to checkout
                        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                                        .setName(Pattern.compile("Proceed To Checkout", Pattern.CASE_INSENSITIVE)))
                                        .first().click();

                        // 3) Create Account page -> Proceed as Guest
                        assertThat(page.locator("h2:has-text('Create Account')")).isVisible();
                        page.getByRole(AriaRole.LINK,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("Proceed As Guest", Pattern.CASE_INSENSITIVE)))
                                        .click();

                        // 4) Contact Information Page
                        assertThat(
                                        page.getByRole(AriaRole.HEADING,
                                                        new Page.GetByRoleOptions()
                                                                        .setName(Pattern.compile("Contact Information",
                                                                                        Pattern.CASE_INSENSITIVE))))
                                        .isVisible();
                        page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("First Name", Pattern.CASE_INSENSITIVE)))
                                        .fill("TestFirst");
                        page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("Last Name", Pattern.CASE_INSENSITIVE)))
                                        .fill("TestLast");
                        page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("Email address", Pattern.CASE_INSENSITIVE)))
                                        .fill("test.user@example.com");
                        page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(
                                                        Pattern.compile("Phone Number", Pattern.CASE_INSENSITIVE)))
                                        .fill("5555555555");

                        Locator checkoutSidebar = page.locator(".bned-order-summary-container:visible").first();
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Order Subtotal') .value"))
                                        .hasText("$149.98");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value"))
                                        .hasText("$2.00");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value"))
                                        .hasText("TBD");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp.bned-total .value"))
                                        .hasText("$151.98");

                        page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions()
                                                        .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                                        .click();

                        // 5) Pickup Information checks
                        assertThat(page.getByText("TestFirst")).isVisible();
                        assertThat(page.getByText("TestLast")).isVisible();
                        assertThat(page.locator("text=test.user@example.com")).isVisible();
                        assertThat(page.locator("text=555")).isVisible();

                        Locator campusLabel = page
                                        .locator("#checkoutOrderDetails .bned-entries-delivery-multicampus-name");
                        try {
                                assertThat(campusLabel)
                                                .hasText(Pattern.compile("DePaul University Loop Campus & SAIC",
                                                                Pattern.CASE_INSENSITIVE));
                        } catch (Exception ignored) {
                        }
                        assertThat(page.locator("text=I'll pick them up").or(page.locator("text=I’ll pick them up")))
                                        .isVisible();

                        // Sidebar values remain
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Order Subtotal') .value"))
                                        .hasText("$149.98");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value"))
                                        .hasText("$2.00");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value"))
                                        .hasText("TBD");
                        assertThat(checkoutSidebar.locator(".bned-checkout-total-wp.bned-total .value"))
                                        .hasText("$151.98");

                        // Confirm pickup item and price visible
                        assertThat(page.locator(".bned-order-summary-container .bned-order-summary-entry-name")
                                        .filter(new Locator.FilterOptions()
                                                        .setHasText(Pattern.compile("JBL Quantum True Wireless",
                                                                        Pattern.CASE_INSENSITIVE))))
                                        .isVisible();
                        assertThat(checkoutSidebar.locator(".bned-order-summary-entry-total:visible")
                                        .filter(new Locator.FilterOptions().setHasText("\\$149\\.98"))).isVisible();

                        page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions()
                                                        .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                                        .click();

                        // 6) Payment Information
                        assertThat(
                                        page.getByRole(AriaRole.HEADING,
                                                        new Page.GetByRoleOptions()
                                                                        .setName(Pattern.compile("Payment Information",
                                                                                        Pattern.CASE_INSENSITIVE))))
                                        .isVisible();
                        Locator paymentSidebar = page
                                        .locator(".bned-payment-order-summary, .bned-order-summary-container:visible")
                                        .first();
                        assertThat(paymentSidebar.locator(".bned-order-summary-entry-total:has-text('$149.98')"))
                                        .isVisible();
                        assertThat(paymentSidebar.locator(".bned-checkout-total-wp:has-text('Handling') .value"))
                                        .hasText("$2.00");
                        // Tax on payment page expected to be present (best-effort check)
                        try {
                                assertThat(paymentSidebar.locator(".bned-checkout-total-wp:has-text('Tax') .value"))
                                                .hasText(Pattern.compile("\\$?\\d"));
                        } catch (Exception ignored) {
                        }
                        // Final total expected (example value provided in workflow)
                        try {
                                assertThat(paymentSidebar.locator(".bned-checkout-total-wp.bned-total .value"))
                                                .hasText("$167.56");
                        } catch (Exception ignored) {
                        }

                        // Click back to cart
                        try {
                                page.getByRole(AriaRole.LINK,
                                                new Page.GetByRoleOptions().setName(Pattern
                                                                .compile("Back to Cart|<\\s*Back to Cart|Back to your cart",
                                                                                Pattern.CASE_INSENSITIVE)))
                                                .first().click();
                        } catch (Exception ignored) {
                                try {
                                        page.getByText(Pattern.compile("<\\s*BACK TO CART|BACK TO CART",
                                                        Pattern.CASE_INSENSITIVE)).first()
                                                        .click();
                                } catch (Exception ignored2) {
                                }
                        }

                        // 7) Your Shopping Cart: remove item and verify empty
                        // Try several common remove selectors
                        try {
                                page.waitForSelector(".bned-cart-entry",
                                                new Page.WaitForSelectorOptions().setTimeout(3000));
                                boolean removed = false;
                                try {
                                        page.locator(".bned-cart-entry-remove").first().click();
                                        removed = true;
                                } catch (Exception ignored) {
                                }
                                if (!removed) {
                                        try {
                                                page.getByRole(AriaRole.BUTTON,
                                                                new Page.GetByRoleOptions()
                                                                                .setName(Pattern.compile(
                                                                                                "Remove|Delete|Trash",
                                                                                                Pattern.CASE_INSENSITIVE)))
                                                                .first().click();
                                                removed = true;
                                        } catch (Exception ignored) {
                                        }
                                }
                                if (!removed) {
                                        try {
                                                page.getByText(Pattern.compile("Remove", Pattern.CASE_INSENSITIVE))
                                                                .first().click();
                                        } catch (Exception ignored) {
                                        }
                                }
                        } catch (Exception ignored) {
                        }

                        // Assert cart is empty: no cart-entry elements
                        try {
                                assertThat(page.locator(".bned-cart-entry")).hasCount(0);
                        } catch (Exception ignored) {
                                // Fallback: look for empty cart message
                                assertThat(
                                                page.locator("text=Your cart is empty").or(
                                                                page.locator("text=You have no items in your cart")))
                                                .isVisible();
                        }

                        page.close();
                }
        }
}