package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.CategoryEntity;

public interface SpendRepository {

    CategoryEntity createCategory(CategoryEntity category);
    void removeCategory(CategoryEntity category);

}
