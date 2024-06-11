package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


@WebTest
public class HttpSpendingTest {

    static {
        Configuration.browserSize = "1920x1080";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue("artem130");
        $("input[name='password']").setValue("7898456");
        $("button[type='submit']").click();
    }

    @Test
    void anotherTest() {
        Selenide.open("http://127.0.0.1:3000/");
        $("a[href*='redirect']").should(visible);
    }

    @Category(
           username = "artem130",
           addCategory = "Обучение2"
    )

    @Spend(
            description = "QA.GURU Advanced 5",
            amount = 65000.00,
            currency = CurrencyValues.RUB
    )

    @Test
    void spendingShouldBeDeletedAfterTableAction(SpendJson spendJson) {
        SelenideElement rowWithSpending = $(".spendings-table tbody")
                .$$("tr")
                .find(text(spendJson.description()));
        $(byText(spendJson.description())).scrollTo();
        rowWithSpending.$$("td").first().click();
        $(".spendings__bulk-actions button").click();
        $(".spendings-table tbody").$$("tr")
                .shouldHave(size(0));
    }
}