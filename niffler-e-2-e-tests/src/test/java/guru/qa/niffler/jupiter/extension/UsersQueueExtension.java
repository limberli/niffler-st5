package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.model.UserJson.simpleUser;

public class UsersQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static final Queue<UserJson> USER_INVITATION_SEND = new ConcurrentLinkedQueue<>();
    private static final Queue<UserJson> USER_INVITATION_RECEIVED = new ConcurrentLinkedQueue<>();
    private static final Queue<UserJson> USER_WITH_FRIENDS = new ConcurrentLinkedQueue<>();

    static {
        USER_INVITATION_SEND.add(simpleUser("Bob", "4567123"));
        USER_INVITATION_RECEIVED.add(simpleUser("Egor", "4567123"));
        USER_WITH_FRIENDS.add(simpleUser("Vita", "4567123"));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Считывает параметры и сохраняет в лист
        List<User.UserType> userListType = Arrays.stream(context.getRequiredTestMethod()
                        .getParameters()).filter(p -> AnnotationSupport.isAnnotated(p, User.class))
                .map(p -> p.getAnnotation(User.class).value())
                .toList();

        List<Method> methods = Arrays.stream(context.getRequiredTestClass()
                        .getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(BeforeEach.class))
                .toList();

        List<User.UserType> beforeEachTypes = methods.stream().flatMap(m -> Arrays.stream(m.getParameters()))
                .filter(p -> AnnotationSupport.isAnnotated(p, User.class))
                .map(p -> p.getAnnotation(User.class).value())
                .toList();

        List<User.UserType> allUserTypes = new ArrayList<>();
        allUserTypes.addAll(userListType);
        allUserTypes.addAll(beforeEachTypes);

        Map<User.UserType, UserJson> users = new HashMap<>();

        UserJson userForTest = null;
        while (userForTest == null) {
            for (User.UserType type : allUserTypes) {
                //переделать switch case java 17
                if (type == User.UserType.INVITATION_SEND) {
                    userForTest = USER_INVITATION_SEND.poll();
                    users.put(User.UserType.INVITATION_SEND, userForTest);
                } else if (type == User.UserType.INVITATION_RECEIVED) {
                    userForTest = USER_INVITATION_RECEIVED.poll();
                    users.put(User.UserType.INVITATION_RECEIVED, userForTest);
                } else if (type == User.UserType.WITH_FRIENDS) {
                    userForTest = USER_WITH_FRIENDS.poll();
                    users.put(User.UserType.WITH_FRIENDS, userForTest);
                }
            }
        }
        Allure.getLifecycle().updateTestCase(testCase -> {
            testCase.setStart(new Date().getTime());
        });
        context.getStore(NAMESPACE).put(context.getUniqueId(), users);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Map<User.UserType, UserJson> usersTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (Map.Entry<User.UserType, UserJson> user : usersTest.entrySet()) {
            if (user.getKey() == User.UserType.INVITATION_SEND) {
                USER_INVITATION_SEND.add(user.getValue());
            } else if (user.getKey() == User.UserType.INVITATION_RECEIVED) {
                USER_INVITATION_RECEIVED.add(user.getValue());
            } else if (user.getKey() == User.UserType.WITH_FRIENDS) {
                USER_WITH_FRIENDS.add(user.getValue());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext
                .getParameter()
                .getType()
                .isAssignableFrom(UserJson.class)
                && parameterContext.getParameter().isAnnotationPresent(User.class); // проверка аннотаций User
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Optional<User> annotation = AnnotationSupport.findAnnotation(parameterContext.getParameter(), User.class);
        //AnnotationSupport.findAnnotation умеет искать аннотация на любом уровне вложенности
        User.UserType userType = annotation.get().value();
        Map<User.UserType, UserJson> user = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);
        return user.get(userType);
    }
}