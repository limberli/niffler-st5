package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.user.DbCreateUserExtension;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith({
        BrowserExtension.class,
        DbCreateUserExtension.class})
public class LoginUpdateTest {

    @Test
    @TestUser
    void doLogin(UserJson user) {
        Selenide.open("http://127.0.0.1:3000/");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.username());
        $("input[name='password']").setValue(user.testData().password());
        $("button[type='submit']").click();
        $(".header__avatar").should(visible);



    }
}