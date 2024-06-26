package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    String repo = System.getProperty("repo");

    static UserRepository getInstance(){
        if ("sjbc".equals(repo)){
            return new UserRepositorySpringJdbc();
        }
        if("hibernate".equals(repo)){
            return new UserRepositoryHibernate();
        }
        return new UserRepositoryJdbc();
    }

    UserAuthEntity createUserInAuth(UserAuthEntity user);
    UserEntity createUserInUserData (UserEntity user);
    Optional<UserEntity> findUserInUserdataById(UUID id);
}