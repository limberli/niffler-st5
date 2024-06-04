package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.*;
import static guru.qa.niffler.jupiter.annotation.User.UserType.*;
import static io.qameta.allure.Allure.step;

@ExtendWith(UsersQueueExtension.class)
public class UserQueueFriendsTest {

    WelcomePage welcomePage = new WelcomePage();
    LoginPage loginPage = new LoginPage();
    PeoplePage peoplePage = new PeoplePage();

    @BeforeEach
    void openBrowser() {
        Selenide.open("http://127.0.0.1:3000/");
        welcomePage.clickLoginButton();

    }

    @Test
    @DisplayName("Отправлен запрос")
    void friendsInvitationSend(@User(INVITATION_SEND) UserJson userForTest,
                               @User(INVITATION_RECEIVED) UserJson anotherUserForTest) {
        step("Авторизация пользователя", () -> {
            loginPage.login(userForTest.username(), userForTest.testData().password());
        });
        step("Кликнуть на иконку «All people»", () -> {
            $x("//a[@href='/people']").click();
        });
        step("Проверить, что в таблице «Actions» отображается статус «Pending invitation»", () -> {
            peoplePage.checkSendInvitation(anotherUserForTest.username());
        });
        step("Нажать на кнопку Logout", () -> {
            $x("//div[@data-tooltip-id='logout']").click();
        });

    }

    @Test
    @DisplayName("Входящий запрос")
    void friendsInvitationReceived(@User(INVITATION_RECEIVED) UserJson userForTest,
                                   @User(INVITATION_SEND) UserJson anotherUserForTest) {
        step("Авторизация пользователя", () -> {
            loginPage.login(userForTest.username(), userForTest.testData().password());
        });
        step("Кликнуть на иконку «All people»", () -> {
            $x("//a[@href='/people']").click();
        });
        step("Проверить, что в таблице «Actions» отображается входящий запрос «Submit invitation»", () -> {
            peoplePage.checkReceiveInvitation(anotherUserForTest.username());
        });
        step("Нажать на кнопку Logout", () -> {
            $x("//div[@data-tooltip-id='logout']").click();
        });
    }

    @Test
    @DisplayName("Друзья")
    void friends(@User(WITH_FRIENDS) UserJson userForTest,
                 @User(INVITATION_SEND) UserJson anotherUserForTest
    ) {
        step("Авторизация пользователя", () -> {
            loginPage.login(userForTest.username(), userForTest.testData().password());
        });
        step("Кликнуть на иконку «All people»", () -> {
            $x("//a[@href='/people']").click();
        });
        step("Проверить, что в таблице «Actions» отображается статус «You are friends»", () -> {
            peoplePage.checkFriends(anotherUserForTest.username());
        });
        step("Нажать на кнопку Logout", () -> {
            $x("//div[@data-tooltip-id='logout']").click();
        });
    }

}