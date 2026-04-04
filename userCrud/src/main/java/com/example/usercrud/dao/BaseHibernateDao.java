package com.example.usercrud.dao;

import com.example.usercrud.config.HibernateUtil;
import com.example.usercrud.exception.DataAccessException;
import com.example.usercrud.exception.UniqueConstraintViolationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Базовая Hibernate-реализация DAO с переиспользуемыми CRUD-операциями.
 *
 * @param <T> тип сущности
 * @param <ID> тип идентификатора
 */
public abstract class BaseHibernateDao<T, ID extends Serializable> implements GenericDao<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(BaseHibernateDao.class);

    private final Class<T> entityClass;

    protected BaseHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        return executeInTransaction("Сохранение " + entityClass.getSimpleName(), session -> {
            session.persist(entity);
            return entity;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            logger.info("Сущность {} получена по id={}", entityClass.getSimpleName(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.error("Ошибка при получении сущности {} по id={}", entityClass.getSimpleName(), id, e);
            throw new DataAccessException(
                    "Не удалось получить " + entityClass.getSimpleName() + " по id=" + id, e
            );
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from " + entityClass.getSimpleName() + " order by id";
            List<T> entities = session.createQuery(hql, entityClass).list();

            logger.info("Получено {} записей типа {}", entities.size(), entityClass.getSimpleName());
            return entities;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка сущностей {}", entityClass.getSimpleName(), e);
            throw new DataAccessException(
                    "Не удалось получить список " + entityClass.getSimpleName(), e
            );
        }
    }

    @Override
    public T update(T entity) {
        return executeInTransaction("обновление " + entityClass.getSimpleName(), session ->
                (T) session.merge(entity)
        );
    }

    @Override
    public boolean deleteById(ID id) {
        return executeInTransaction("удаление " + entityClass.getSimpleName(), session -> {
            T entity = session.get(entityClass, id);
            if (entity == null) {
                logger.warn("Сущность {} с id={} не найдена для удаления", entityClass.getSimpleName(), id);
                return false;
            }

            session.remove(entity);
            return true;
        });
    }

    /**
     * Выполняет Hibernate-операцию внутри транзакции.
     * Берёт на себя commit, rollback и преобразование исключений.
     *
     * @param operationName имя операции для логирования
     * @param action выполняемое действие
     * @return результат операции
     * @param <R> тип результата
     */
    protected <R> R executeInTransaction(String operationName, Function<Session, R> action) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            R result = action.apply(session);

            transaction.commit();
            logger.info("Операция '{}' успешно завершена", operationName);
            return result;

        } catch (ConstraintViolationException e) {
            rollback(transaction);

            logger.warn(
                    "Нарушение ограничения при операции '{}': SQLState={}, constraint={}",
                    operationName,
                    e.getSQLState(),
                    e.getConstraintName()
            );

            if ("23505".equals(e.getSQLState())) {
                throw new UniqueConstraintViolationException(
                        "Нарушено ограничение на уникальность",
                        e.getConstraintName(),
                        e
                );
            }

            throw new DataAccessException("Ошибка целостности данных при операции: " + operationName, e);

        } catch (HibernateException e) {
            rollback(transaction);
            logger.error("Ошибка Hibernate при операции '{}'", operationName, e);
            throw new DataAccessException("Сбой базы данных при операции: " + operationName, e);

        } catch (Exception e) {
            rollback(transaction);
            logger.error("Непредвиденная ошибка при операции '{}'", operationName, e);
            throw new DataAccessException("Не удалось выполнить операцию: " + operationName, e);
        }
    }

    /**
     * Безопасно откатывает транзакцию.
     *
     * @param transaction транзакция
     */
    private void rollback(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.info("Транзакция успешно откатилась");
            }
        } catch (Exception e) {
            logger.error("Ошибка при откате транзакции", e);
        }
    }

    /**
     * Возвращает класс сущности.
     *
     * @return класс сущности
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }
}