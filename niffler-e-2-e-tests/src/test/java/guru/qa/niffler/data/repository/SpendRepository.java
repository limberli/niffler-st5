package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

public interface SpendRepository {

    String repo = System.getProperty("repo");

    static SpendRepository getInstance(){
        if ("sjbc".equals(repo)){
            return new SpendRepositorySpringJdbc();
        }
        if("hibernate".equals(repo)){
            return new SpendRepositoryHibernate();
        }
        return new SpendRepositoryJdbc();
    }

    CategoryEntity createCategory(CategoryEntity category);
    CategoryEntity editCategory (CategoryEntity category);
    void removeCategory(CategoryEntity category);
    SpendEntity createSpend(SpendEntity spend);
    SpendEntity editSpend(SpendEntity spend);
    void removeSpend(SpendEntity spend);

}