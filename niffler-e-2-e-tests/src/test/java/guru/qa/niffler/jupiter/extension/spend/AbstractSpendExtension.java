package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.extension.category.AbstractCategoryExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;


public abstract class AbstractSpendExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(AbstractSpendExtension.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        CategoryJson category = extensionContext.getStore(AbstractCategoryExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), CategoryJson.class);

        AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                Spend.class
        ).ifPresent(
                sp -> {
                    SpendJson spend = new SpendJson(
                            null,
                            new Date(),
                            category.category(),
                            sp.currency(),
                            sp.amount(),
                            sp.description(),
                            category.username(),
                            category.id()
                    );

                    spend = createSpend(spend);

                    extensionContext.getStore(NAMESPACE).put(
                            extensionContext.getUniqueId(), spend);
                }

        );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SpendJson spendJson = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);
        removeSpend(spendJson);
    }

    //Определяет, поддерживается ли тип параметра
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }


    //Возвращает параметр для выполнения теста
    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId());
    }

    protected abstract SpendJson createSpend(SpendJson spendJson);
    protected abstract void removeSpend(SpendJson spendJson);
}