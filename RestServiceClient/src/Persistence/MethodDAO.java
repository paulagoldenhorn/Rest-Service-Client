package Persistence;

import Entity.Method;
import Exce.DAOException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class MethodDAO extends DAOManager<Method> {
    private static final String INSERT = "INSERT INTO METHOD (ID, TYPE, DESCRIPTION) VALUES (?,?,?)";
    private static final String SELECT = "SELECT * FROM METHOD WHERE ID=?";
    private static final String SELECT_ALL = "SELECT * FROM METHOD";
    private static final String UPDATE = "UPDATE METHOD SET TYPE=?, DESCRIPTION=? WHERE ID=?";
    private static final String DELETE = "DELETE FROM METHOD WHERE ID=?";
    @Override
    public Method create(Method method) throws DAOException {
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(INSERT);
            ps.setInt(1, method.getId());
            ps.setString(2, method.getType());
            ps.setString(3, method.getDescription());

            ps.executeUpdate();

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO SAVE ERROR: " + e.getMessage());
        }

        return method;
    }

    @Override
    public Method read(Integer id) throws DAOException {
        Method method = new Method();
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(SELECT);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                method.setId(id);
                method.setType(rs.getString("TYPE"));
                method.setDescription(rs.getString("DESCRIPTION"));
            }

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO GET ERROR: " + e.getMessage());
        }

        return method;
    }

    @Override
    public List<Method> readAll() throws DAOException {
        List<Method> methodList = new LinkedList<>();
        try (Connection connection = getConnection()) {

            Statement stat = connection.createStatement();

            ResultSet rs = stat.executeQuery(SELECT_ALL);

            while (rs.next())
            {
                Method method = new Method();
                method.setId(rs.getInt("ID"));
                method.setType(rs.getString("TYPE"));
                method.setDescription(rs.getString("DESCRIPTION"));

                methodList.add(method);
            }

            stat.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO GET ALL ERROR: " + e.getMessage());
        }
        return methodList;
    }

    @Override
    public Method update(Method method) throws DAOException {
        try (Connection connection = getConnection()) {

            PreparedStatement prepStat = connection.prepareStatement(UPDATE);
            prepStat.setString(1, method.getType());
            prepStat.setString(2, method.getDescription());
            prepStat.setInt(3, method.getId());

            prepStat.executeUpdate();

            prepStat.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO UPDATE ERROR: " + e.getMessage());
        }
        return method;
    }

    @Override
    public void delete(Integer id) throws DAOException {
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(DELETE);
            ps.setInt(1, id);

            ps.executeUpdate();

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO DELETE ERROR: " + e.getMessage());
        }
    }

    public Method readByType(String type) throws DAOException {
        Method method = new Method();
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM METHOD WHERE TYPE=?");
            ps.setString(1, type);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                method.setId(rs.getInt("ID"));
                method.setType(type);
                method.setDescription(rs.getString("DESCRIPTION"));
            }

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("MethodDAO GET BY TYPE ERROR: " + e.getMessage());
        }

        return method;
    }
}
