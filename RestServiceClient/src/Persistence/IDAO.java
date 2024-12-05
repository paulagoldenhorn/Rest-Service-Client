package Persistence;

import Exce.DAOException;

import java.util.List;

public interface IDAO<T> {
    T create(T t) throws DAOException;
    T read(Integer id) throws DAOException;
    List<T> readAll() throws DAOException;
    T update(T t) throws DAOException;
    void delete(Integer id) throws DAOException;
}
