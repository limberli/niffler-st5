package guru.qa.niffler.data.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionalEntityManager implements EntityManager {

   /* TransactionalEntityManager, является декоратором для EntityManager,
    который добавляет управление транзакциями к методам EntityManager.*/

    /**
     * Декоратор - Он часто используется для того, чтобы расширять возможности объектов без изменения их кода.
     *
     */

    private final EntityManager delegate;

   /* Поле delegate: Это экземпляр EntityManager, который выполняет фактическую работу.
    TransactionalEntityManager добавляет к нему дополнительную логику для обработки транзакций.*/

    public TransactionalEntityManager(EntityManager delegate) {
        this.delegate = delegate;
    }

   //Конструктор: Конструктор принимает EntityManager и сохраняет его в поле delegate.

    private void tx(Consumer<EntityManager> consumer){
        EntityTransaction entityTransaction = delegate.getTransaction();
        entityTransaction.begin();
        try {
            consumer.accept(delegate);
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            throw e;
        }
    }

    /* Метод tx: Этот метод принимает Consumer<EntityManager> и оборачивает его в транзакцию. Он:
    Начинает транзакцию.
    Выполняет операцию, переданную в виде лямбды.
    Фиксирует транзакцию, если операция успешна, или откатывает ее в случае исключения.*/

    private <T> T txWithResult(Function<EntityManager, T> consumer){
        EntityTransaction entityTransaction = delegate.getTransaction();
        entityTransaction.begin();
        try {
            T result = consumer.apply(delegate);
            entityTransaction.commit();
            return result;
        } catch (Exception e) {
            entityTransaction.rollback();
            throw e;
        }
    }

    /*Метод txWithResult: Этот метод аналогичен tx, но предназначен для операций, которые возвращают результат.
    Он принимает Function<EntityManager, T> и:
    Начинает транзакцию.
    Выполняет операцию и возвращает результат.
    Фиксирует транзакцию или откатывает ее в случае исключения.*/

    /*Методы persist, merge и remove: Эти методы реализуют методы EntityManager,
    добавляя к ним управление транзакциями с использованием tx и txWithResult.*/

    @Override
    public void persist(Object entity) {
        tx(em -> delegate.persist(entity));
    }
  /*  Когда вызывается persist, он передает лямбду em -> delegate.persist(entity) методу tx,
    который оборачивает вызов persist в транзакцию.*/

    @Override
    public <T> T merge(T entity) {
        return txWithResult(em -> em.merge(entity));
    }

    /*Когда вызывается merge, он передает лямбду em -> em.merge(entity) методу txWithResult,
    который оборачивает вызов merge в транзакцию и возвращает результат.*/

    @Override
    public void remove(Object entity) {
        tx(em -> delegate.remove(entity));
    }

    /*Когда вызывается remove, он передает лямбду em -> delegate.remove(entity) методу tx,
    который оборачивает вызов remove в транзакцию.*/

    @Override
    public <T> T find(Class<T> aClass, Object o) {
        return delegate.find(aClass, o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, Map<String, Object> map) {
        return delegate.find(aClass, o, map);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType) {
        return delegate.find(aClass, o, lockModeType);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType, Map<String, Object> map) {
        return delegate.find(aClass, o, lockModeType, map);
    }

    @Override
    public <T> T getReference(Class<T> aClass, Object o) {
        return delegate.getReference(aClass, o);
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushModeType) {
        delegate.setFlushMode(flushModeType);
    }

    @Override
    public FlushModeType getFlushMode() {
        return delegate.getFlushMode();
    }

    @Override
    public void lock(Object o, LockModeType lockModeType) {
        delegate.lock(o, lockModeType);
    }

    @Override
    public void lock(Object o, LockModeType lockModeType, Map<String, Object> map) {
        delegate.lock(o, lockModeType, map);
    }

    @Override
    public void refresh(Object o) {
        delegate.refresh(o);
    }

    @Override
    public void refresh(Object o, Map<String, Object> map) {
        delegate.refresh(o, map);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType) {
        delegate.refresh(o, lockModeType);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType, Map<String, Object> map) {
        delegate.refresh(o, lockModeType, map);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void detach(Object o) {
        delegate.detach(o);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public LockModeType getLockMode(Object o) {
        return delegate.getLockMode(o);
    }

    @Override
    public void setProperty(String s, Object o) {
        delegate.setProperty(s, o);
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Query createQuery(String s) {
        return delegate.createQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return delegate.createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate criteriaUpdate) {
        return delegate.createQuery(criteriaUpdate);
    }

    @Override
    public Query createQuery(CriteriaDelete criteriaDelete) {
        return delegate.createQuery(criteriaDelete);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String s, Class<T> aClass) {
        return delegate.createQuery(s, aClass);
    }

    @Override
    public Query createNamedQuery(String s) {
        return delegate.createNamedQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String s, Class<T> aClass) {
        return delegate.createNamedQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s) {
        return delegate.createNativeQuery(s);
    }

    @Override
    public Query createNativeQuery(String s, Class aClass) {
        return delegate.createNativeQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s, String s1) {
        return delegate.createNativeQuery(s, s1);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String s) {
        return delegate.createNamedStoredProcedureQuery(s);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s) {
        return delegate.createStoredProcedureQuery(s);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s, Class... classes) {
        return delegate.createStoredProcedureQuery(s, classes);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s, String... strings) {
        return delegate.createStoredProcedureQuery(s, strings);
    }

    @Override
    public void joinTransaction() {
        delegate.joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return delegate.isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return delegate.unwrap(aClass);
    }

    @Override
    public Object getDelegate() {
        return delegate.getDelegate();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return delegate.getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> aClass) {
        return delegate.createEntityGraph(aClass);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String s) {
        return delegate.createEntityGraph(s);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String s) {
        return delegate.getEntityGraph(s);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> aClass) {
        return delegate.getEntityGraphs(aClass);
    }
}