package dao;

import java.util.List;

public interface DAO<T> {
    T findById(long id);

    List<T> findAll();

    T update(T dto);

    T create(T dto);

    T delete(long id);
}
