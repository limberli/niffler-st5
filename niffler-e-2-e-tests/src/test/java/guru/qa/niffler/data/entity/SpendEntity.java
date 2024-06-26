package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class SpendEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private Date spendDate;
    private Double amount;
    private String description;
    private CategoryEntity category;

    //Преобразование данных в Entity из JSON
    public static SpendEntity fromJson(SpendJson spendJson) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(spendJson.categoryId());

        SpendEntity spend = new SpendEntity();

        spend.setId(spendJson.id());
        spend.setUsername(spendJson.username());
        spend.setCurrency(spendJson.currency());
        spend.setSpendDate(spendJson.spendDate());
        spend.setAmount(spendJson.amount());
        spend.setDescription(spendJson.description());
        spend.setCategory(categoryEntity);
        return spend;
    }
}