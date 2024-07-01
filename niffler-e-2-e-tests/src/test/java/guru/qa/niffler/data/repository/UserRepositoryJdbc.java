package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.CurrencyValues;
import guru.qa.niffler.data.entity.UserAuthEntity;
import guru.qa.niffler.data.entity.UserEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

    private static final DataSource authDataSource = DataSourceProvider.dataSource(DataBase.AUTH);
    private static final DataSource udDataSource = DataSourceProvider.dataSource(DataBase.USERDATA);

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserAuthEntity createUserInAuth(UserAuthEntity user) {
        try (Connection conn = authDataSource.getConnection()) {
            conn.setAutoCommit(false);
            //Отменяет автоматически изменения когда выполнился запрос
            try (PreparedStatement userPs = conn.prepareStatement(
                    "INSERT INTO \"user\" (" +
                            "username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO \"authority\" (" +
                                 "user_id, authority) " +
                                 "VALUES (?, ?)"
                 )) {
                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());

                userPs.executeUpdate();

                UUID generatedUserId;
                try (ResultSet resultSet = userPs.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedUserId = UUID.fromString(resultSet.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t access to id");
                    }
                }
                user.setId(generatedUserId);

                for (Authority a : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, a.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                    //Нужно после каждого бадча делать clearParameters
                }

                authorityPs.executeBatch();

                conn.commit();

                return user;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
                //rollback - все изменения которые сделали в запросе должны сделать откат
            } finally {
                conn.setAutoCommit(true);
                //AutoCommit вернуть в первоначальное состояние
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity createUserInUserData(UserEntity user) {
        try (Connection conn = udDataSource.getConnection();
             PreparedStatement userPs = conn.prepareStatement(
                     "INSERT INTO \"user\" (" +
                             "username, currency, firstname, surname, photo, photo_small) " +
                             "VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setObject(5, user.getPhoto());
            userPs.setObject(6, user.getPhotoSmall());

            userPs.executeUpdate();

            UUID generatedUserId;
            try (ResultSet resultSet = userPs.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedUserId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalStateException("Can`t access to id");
                }
            }
            user.setId(generatedUserId);
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserAuthEntity updateUserInAuth(UserAuthEntity user) {
        //BatchUpdate - позволяют модифицировать несколько строк в одной операции (если связанные таблицы)
        try (Connection conn = authDataSource.getConnection()) {
            conn.setAutoCommit(false);
            //Отменяет автоматически изменения когда выполнился запрос
            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                            "account_non_locked =?, credentials_non_expired = ? WHERE id =?");

                 PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM \"authority\" WHERE user_id = ?");
                 //Для чего нужно удалять ?


                 PreparedStatement authorityPs = conn.prepareStatement(
                         "UPDATE \"authority\" SET authority = ? WHERE user_id = ?"

                 )) {
                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());
                userPs.setObject(7, user.getId());

                userPs.executeUpdate();

                deleteUser.setObject(1, user.getId());
                deleteUser.executeUpdate();

                for (Authority a : Authority.values()) {
                    authorityPs.setString(1, a.name());
                    authorityPs.setObject(2, user.getId());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                    //Нужно после каждого Batch делать clearParameters
                }

                authorityPs.executeBatch();
                conn.commit();
                return user;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
                //rollback - все изменения которые сделали в запросе должны сделать откат
            } finally {
                conn.setAutoCommit(true);
                //AutoCommit вернуть в первоначальное состояние
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity updateUserInUserData(UserEntity user) {
        try (Connection conn = udDataSource.getConnection();
             PreparedStatement userPs = conn.prepareStatement(
                     "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, " +
                             "surname = ?, photo = ?, photo_small =? WHERE id = ?"
             )) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setObject(5, user.getPhoto());
            userPs.setObject(6, user.getPhotoSmall());
            userPs.setObject(7, user.getId());

            userPs.executeUpdate();

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findUserInUserdataById(UUID id) {
        UserEntity user = new UserEntity();
        try (Connection conn = udDataSource.getConnection();
             PreparedStatement userPs = conn.prepareStatement(
                     "SELECT * FROM \"user\" WHERE id = ?"
                     )) {
            userPs.setObject(1, id);
            userPs.execute();

            try (ResultSet resultSet = userPs.getResultSet()){
                if (resultSet.next()) {
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    user.setFirstname(resultSet.getString("firstname"));
                    user.setSurname(resultSet.getString("surname"));
                    user.setPhoto(resultSet.getBytes("photo"));
                    user.setPhotoSmall(resultSet.getBytes("photo_small"));
                } else {
                    return Optional.empty();
                }
            }
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(user);
    }
}