package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class WelcomePage {

    private final SelenideElement loginButton = $x("//a[.='Login']");

    public void clickLoginButton() {
        loginButton.click();
    }

}
