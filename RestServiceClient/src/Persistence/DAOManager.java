package Persistence;

import Exce.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class DAOManager<T> implements IDAO<T> {
    private static final String DRIVER = "org.h2.Driver";
    private static final String URL = "jdbc:h2:~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER); // Gets driver
        return DriverManager.getConnection(URL, USER, PASSWORD); // Gets connection
    }

    public abstract T create(T t) throws DAOException;
    public abstract T read(Integer id) throws DAOException;
    public abstract List<T> readAll() throws DAOException;
    public abstract T update(T t) throws DAOException;
    public abstract void delete(Integer id) throws DAOException;
}
