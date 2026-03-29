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

public abstract class BaseHibernateDao<T, ID extends Serializable> implements GenericDao<T, ID> {
    private static final Logger logger = LoggerFactory.getLogger(BaseHibernateDao.class);

    private final Class<T> entityClass;

    protected BaseHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        return executeInTransaction("save " + entityClass.getSimpleName(), session -> {
            session.persist(entity);
            return entity;
        });
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            T entity = session.get(entityClass, id);
            logger.info("{} fetched by id={}", entityClass.getSimpleName(), id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.error("Error fetching {} by id={}", entityClass.getSimpleName(), id, e);
            throw new DataAccessException(
                    "Failed to fetch " + entityClass.getSimpleName() + " by id=" + id, e
            );
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from " + entityClass.getSimpleName() + " order by id";
            List<T> entities = session.createQuery(hql, entityClass).list();

            logger.info("Fetched {} {} entities", entities.size(), entityClass.getSimpleName());
            return entities;
        } catch (Exception e) {
            logger.error("Error fetching all {} entities", entityClass.getSimpleName(), e);
            throw new DataAccessException(
                    "Failed to fetch " + entityClass.getSimpleName() + " list", e
            );
        }
    }

    @Override
    public T update(T entity) {
        return executeInTransaction("update " + entityClass.getSimpleName(), session ->
                (T) session.merge(entity)
        );
    }

    @Override
    public boolean deleteById(ID id) {
        return executeInTransaction("delete " + entityClass.getSimpleName(), session -> {
            T entity = session.get(entityClass, id);
            if (entity == null) {
                logger.warn("{} with id={} not found for deletion", entityClass.getSimpleName(), id);
                return false;
            }

            session.remove(entity);
            return true;
        });
    }

    protected <R> R executeInTransaction(String operationName, Function<Session, R> action) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            R result = action.apply(session);

            transaction.commit();
            logger.info("Operation '{}' completed successfully", operationName);
            return result;

        } catch (ConstraintViolationException e) {
            rollback(transaction);

            logger.warn(
                    "Constraint violation during '{}': SQLState={}, constraint={}",
                    operationName,
                    e.getSQLState(),
                    e.getConstraintName()
            );

            if ("23505".equals(e.getSQLState())) {
                throw new UniqueConstraintViolationException(
                        "Unique constraint violated",
                        e.getConstraintName(),
                        e
                );
            }

            throw new DataAccessException("Data integrity error during operation: " + operationName, e);

        } catch (HibernateException e) {
            rollback(transaction);
            logger.error("Hibernate error during '{}'", operationName, e);
            throw new DataAccessException("Database failure during operation: " + operationName, e);

        } catch (Exception e) {
            rollback(transaction);
            logger.error("Unexpected error during '{}'", operationName, e);
            throw new DataAccessException("Failed to execute operation: " + operationName, e);
        }
    }

    private void rollback(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.info("Transaction rolled back successfully");
            }
        } catch (Exception e) {
            logger.error("Error during transaction rollback", e);
        }
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}