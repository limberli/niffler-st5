package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApi;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(CategoryExtension.class);
    /**
     NAMESPACE - это хранилище. Для создания и хранения объектов.
     */

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("http://127.0.0.1:8093/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        //ExtensionContext - это объект где есть все информация о тесте
        //Также он не используется в другие тестах (Потокобезопасный объект)
        //Достаем из него аннотации, имя теста, список параметров и т.д.
        CategoryApi categoryApi = retrofit.create(CategoryApi.class);
                AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
               //Возвращает информацию о тестовом методе, который сейчас запускается
                Category.class
        ).ifPresent(
                //Если @Category найдена, тогда передаю информацию в лямду
                category -> {
                    CategoryJson categoryJson = new CategoryJson(
                           //Записываю данные в модель categoryJson
                            null,
                            category.addCategory(),
                            category.username()
                            //Из лямды достаю данные, которые в ней записаны
                    );
                    try {
                      CategoryJson result =  categoryApi.createCategory(categoryJson).execute().body();
                      //Записанную модель я отправляю в API клиент и получаю результат
                      extensionContext.getStore(NAMESPACE).put("category", result);
                      //extensionContext позволяет хранить и передавать данные из разных места из .getStore (хэш-мапа)
                      // если передать разные объекты, в результате получим разные хэш-мапы
                      //когда получили результат мы сохраняем в extensionContext, что мы создали Category
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        //supportsParameter не обходим чтобы достать параметры в тест
        //Как работает?
        //1. Вызывается supportsParameter который проверяет что тип параметра соответствует тип CategoryJson.class == parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("category");
    }
}
