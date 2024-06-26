package guru.qa.niffler.jupiter.extension.category;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;


public abstract class AbstractCategoryExtension implements BeforeEachCallback, AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(AbstractCategoryExtension.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                Category.class
        ).ifPresent(
                cat -> {
                    CategoryJson category = new CategoryJson(
                            null,
                            cat.addCategory(),
                            cat.username()
                    );

                    category = createCategory(category);

                    extensionContext.getStore(NAMESPACE).put(
                            extensionContext.getUniqueId(), category);
                }

        );

    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson categoryJson = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        removeCategory(categoryJson);

    }

    protected abstract CategoryJson createCategory(CategoryJson categoryJson);

    protected abstract void removeCategory(CategoryJson categoryJson);
}