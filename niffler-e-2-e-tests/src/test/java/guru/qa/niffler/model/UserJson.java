package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import guru.qa.niffler.data.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        byte[] photo,
        @JsonProperty("photoSmall")
        byte[] photoSmall,
        @JsonProperty("friendState")
        FriendState friendState,
        @JsonIgnore
        TestData testData) {

    public static UserJson simpleUser(String username, String password) {
        return new UserJson(
                null,
                username,
                null,
                null,
                null,
                null,
                null,
                null,
                new TestData(
                        password
                )
        );
    }

    public static UserJson fromEntity(UserEntity userEntity, String password) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                userEntity.getCurrency(),
                //Почему не совместимые типы?
                userEntity.getPhoto(),
                userEntity.getPhotoSmall(),
                null,
                new TestData(password)
        );
    }

    public static UserJson randomUser(){
        Faker faker = new Faker();

        return new UserJson(
                null,
                faker.name().username(),
                faker.name().firstName(),
                faker.name().lastName(),
                CurrencyValues.EUR,
                faker.avatar().image().getBytes(StandardCharsets.UTF_8),
                faker.avatar().image().getBytes(StandardCharsets.UTF_8),
                null,
                new TestData(faker.internet().password())
        );
    }
}