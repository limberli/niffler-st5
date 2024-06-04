package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.opentelemetry.api.logs.Severity;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class PeoplePage {
    private final ElementsCollection peopleRows = $$x("//table[@class='table abstract-table']/tbody/tr");
    private final String submitInvitation = ".//div[@data-tooltip-content='Submit invitation']";

    public void checkSendInvitation(String username) {
        SelenideElement td = findUserByName(username);
        td.shouldHave(text("Pending invitation"));
    }

    public void checkReceiveInvitation(String username) {
        SelenideElement td = findUserByName(username);
        td.$(By.xpath(submitInvitation)).shouldBe(visible);
    }

    public void checkFriends(String username){
        SelenideElement td = findUserByName(username);
        td.shouldHave(text("You are friends"));
    }

    private SelenideElement findUserByName(String username) {
        return peopleRows.find(text(username)).$$("td").last();
    }

}