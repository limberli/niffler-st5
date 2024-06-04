package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.model.UserJson.simpleUser;

public class UserQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static final Queue<UserJson> USERS = new ConcurrentLinkedQueue<>();

    static {
        USERS.add(simpleUser("Bob","4567123"));
        USERS.add(simpleUser("Egor","4567123"));
        USERS.add(simpleUser("Vita","4567123"));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserJson userForTest = null;
        while (userForTest == null) {
            userForTest = USERS.poll();
        }
        Allure.getLifecycle().updateTestCase(testCase -> {
            testCase.setStart(new Date().getTime());
        });
        context.getStore(NAMESPACE).put(context.getUniqueId(), userForTest);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
       UserJson userFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
       USERS.add(userFromTest);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }
}
