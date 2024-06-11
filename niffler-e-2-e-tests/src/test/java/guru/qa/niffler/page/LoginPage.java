package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class LoginPage {

    private final SelenideElement usernameInput = $x("//input[@name='username']");
    private final SelenideElement passwordInput = $x("//input[@name='password']");
    private final SelenideElement submitButton = $x("//button[@type='submit']");

    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password){
        passwordInput.setValue(password);
        return this;
    }

    public void clickSubmitButton(){
        submitButton.click();
    }

    public void login(String username, String password){
        setUsername(username).setPassword(password).clickSubmitButton();
    }
}