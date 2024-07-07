package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import guru.qa.niffler.model.CurrencyValues;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final DataSource spendDataSource = DataSourceProvider.dataSource(DataBase.SPEND);

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO category (category, username) VALUES (?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, category.getCategory());
            ps.setString(2, category.getUsername());
            ps.executeUpdate();

            UUID generatedId;
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalStateException("Can`t access to id");
                }
            }
            category.setId(generatedId);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity editCategory(CategoryEntity category) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE category SET category = ?, username = ? WHERE id =?")) {
            ps.setString(1, category.getCategory());
            ps.setString(2, category.getUsername());
            ps.setObject(3, category.getId());
            ps.executeUpdate();

            return category;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM category WHERE id = ?"
             )) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.executeUpdate();

            UUID generatedId;
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalStateException("Can`t access to id");
                }
            }
            spend.setId(generatedId);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity editSpend(SpendEntity spend) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ? WHERE id =?")) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getId());
            ps.executeUpdate();

            return spend;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM spend WHERE id = ?"
             )) {
            ps.setObject(1, spend.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity findByUsernameAndCategory(String username, String category) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("SELECT * FROM \"category\" where category = ? and username = ?;",
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {
            statement.setString(1, category);
            statement.setString(2, username);
            statement.execute();

            CategoryEntity categoryEntity = new CategoryEntity();
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    categoryEntity.setId(UUID.fromString(resultSet.getString("id")));
                    categoryEntity.setUsername(resultSet.getString("username"));
                    categoryEntity.setCategory(resultSet.getString("category"));
                } else {
                    throw new IllegalArgumentException("Can`t access to category");
                }
            }
            return categoryEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllSpendsByUsername(String username) {
        try (Connection conn = spendDataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     """
                             SELECT * FROM "spend" where username = ?
                             """)) {
            ps.setString(1, username);
            ps.executeQuery();

            List<SpendEntity> spendList = new ArrayList<>();

            try (ResultSet resultSet = ps.getResultSet()) {
                while (resultSet.next()) {
                    SpendEntity spend = new SpendEntity();

                    spend.setId(resultSet.getObject("id", UUID.class));
                    spend.setUsername(resultSet.getString("username"));
                    spend.setSpendDate(resultSet.getDate("spend_date"));
                    spend.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    spend.setAmount(resultSet.getDouble("amount"));
                    spend.setDescription(resultSet.getString("description"));
                    spend.setCategory((CategoryEntity) resultSet.getObject("category_id"));

                    spendList.add(spend);
                }
                return spendList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}