package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.extension.category.JdbcCategoryExtension;
import guru.qa.niffler.jupiter.extension.spend.JdbcSpendExtension;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.model.CurrencyValues.RUB;

@ExtendWith({JdbcCategoryExtension.class, JdbcSpendExtension.class})

public class JdbcSpendingTest {

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

    @Category(username = "artem130", addCategory = "Обучение9")
    @Spend(description = "QA.GURU Advanced 5", amount = 65000.00, currency = RUB)


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