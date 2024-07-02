package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.CategoryJson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
//Entity не может работать без аннотации ID
@Table(name = "category")
public class CategoryEntity implements Serializable {

    @Id //Id используется по какому полю будет сопоставление с БД
    @GeneratedValue(strategy = GenerationType.AUTO) //Используется в том случаи если Id нужно генерировать
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private String username;

    //Преобразование данных в Entity из JSON
    public static CategoryEntity fromJson(CategoryJson categoryJson){
        CategoryEntity entity = new CategoryEntity();
        entity.setId(categoryJson.id());
        entity.setCategory(categoryJson.category());
        entity.setUsername(categoryJson.username());
        return entity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CategoryEntity that = (CategoryEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}