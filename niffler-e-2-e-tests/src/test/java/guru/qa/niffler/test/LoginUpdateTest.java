package guru.qa.niffler.test;

import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.data.repository.UserRepositorySpringJdbc;
import org.junit.jupiter.api.Test;

public class LoginUpdateTest {

    UserRepository userRepository = new UserRepositorySpringJdbc();

    UserEntity userDataUser;

    @Test
    void doLogin () {
        userRepository.findUserInUserdataById(userDataUser.getId());
        System.out.println();
    }
}
