package guru.qa.niffler.jupiter.extension;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.type.TimeOfDayOrBuilder;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.UserType.*;
import static guru.qa.niffler.model.UserJson.simpleUser;

public class UsersQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static final Map<User.UserType, ConcurrentLinkedQueue<UserJson>> USERS = new ConcurrentHashMap<>();

    static {
        USERS.put(INVITATION_SEND, new ConcurrentLinkedQueue<>());
        USERS.put(INVITATION_RECEIVED, new ConcurrentLinkedQueue<>());
        USERS.put(WITH_FRIENDS, new ConcurrentLinkedQueue<>());

        USERS.get(INVITATION_SEND).add(simpleUser("Bob", "4567123"));
        USERS.get(INVITATION_RECEIVED).add(simpleUser("Egor", "4567123"));
        USERS.get(WITH_FRIENDS).add(simpleUser("Vita", "4567123"));

    // Не работает в 3-х потоках
    //    USERS.get(INVITATION_SEND).add(simpleUser("Mark", "4567123"));
    //    USERS.get(INVITATION_RECEIVED).add(simpleUser("Alex", "4567123"));
    //    USERS.get(WITH_FRIENDS).add(simpleUser("Vlad", "4567123"));
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
       //Перезаписывает ключ
        Map<User.UserType, UserJson> users = new HashMap<>();

        for (User.UserType type : allUserTypes) {
        UserJson userForTest = null;
        while (userForTest == null) {
                //переделать switch case java 17
                if (type == INVITATION_SEND) {
                    userForTest = USERS.get(INVITATION_SEND).poll();
                } else if (type == INVITATION_RECEIVED) {
                    userForTest = USERS.get(INVITATION_RECEIVED).poll();
                } else if (type == WITH_FRIENDS) {
                    userForTest = USERS.get(WITH_FRIENDS).poll();
                }
            }
            users.put(type, userForTest);
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
            if (user.getKey() == INVITATION_SEND) {
                USERS.get(INVITATION_SEND).add(user.getValue());
            } else if (user.getKey() == INVITATION_RECEIVED) {
                USERS.get(INVITATION_RECEIVED).add(user.getValue());
            } else if (user.getKey() == WITH_FRIENDS) {
                USERS.get(WITH_FRIENDS).add(user.getValue());
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