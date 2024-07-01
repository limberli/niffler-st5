package guru.qa.niffler.jupiter.extension.user;

import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.model.UserJson;

public class DbCreateUserExtension extends CreateUserExtension{

    public final UserRepository userRepository = UserRepository.getInstance();

    @Override
    UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        UserAuthEntity userAuthEntity = UserAuthEntity.fromJson(user);
        userRepository.createUserInAuth(userAuthEntity);
        userRepository.createUserInUserData(userEntity);
        return UserJson.fromEntity(userEntity, user.testData().password());
    }
}