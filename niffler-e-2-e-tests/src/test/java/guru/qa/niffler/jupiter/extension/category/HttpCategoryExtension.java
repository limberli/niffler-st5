package guru.qa.niffler.jupiter.extension.category;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class HttpCategoryExtension extends AbstractCategoryExtension {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://127.0.0.1:8093/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    @Override
    protected CategoryJson createCategory(CategoryJson categoryJson) {

        SpendApi spendApi = retrofit.create(SpendApi.class);

        try {
            return spendApi.createCategory(categoryJson).execute().body();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void removeCategory(CategoryJson categoryJson) {

    }

}