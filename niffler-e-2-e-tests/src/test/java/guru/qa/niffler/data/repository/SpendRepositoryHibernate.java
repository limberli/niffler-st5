package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.jpa.EmProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class SpendRepositoryHibernate implements SpendRepository{

    private final EntityManager em = EmProvider.entityManager(DataBase.SPEND);

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        em.persist(category);
        return category;
    }

    @Override
    public CategoryEntity editCategory(CategoryEntity category) {
        return em.merge(category);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        em.remove(category);
    }

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        em.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity editSpend(SpendEntity spend) {
        return em.merge(spend);
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        em.remove(spend);
    }

    @Override
    public CategoryEntity findByUsernameAndCategory(String username, String category) {
        return em.createQuery("FROM CategoryEntity WHERE category = :category and username = :username", CategoryEntity.class)
                .setParameter("category", category)
                .setParameter("username", username)
                .getSingleResult();
    }

    @Override
    public List<SpendEntity> findAllSpendsByUsername(String username) {
        return em.createQuery("FROM SpendEntity WHERE username = :username", SpendEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

}