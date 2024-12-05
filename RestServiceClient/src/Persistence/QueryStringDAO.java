package Persistence;

import Entity.QueryString;
import Exce.DAOException;

import java.sql.*;
import java.util.*;

public class QueryStringDAO extends DAOManager<QueryString> {

    private static final String INSERT = "INSERT INTO QUERY_STRINGS (QUERY_KEY, QUERY_VALUE, REQUEST_ID) VALUES (?,?,?)";
    private static final String SELECT = "SELECT * FROM QUERY_STRINGS WHERE ID=?";
    private static final String SELECT_ALL = "SELECT * FROM QUERY_STRINGS";
    private static final String UPDATE = "UPDATE QUERY_STRINGS SET QUERY_KEY=?, QUERY_VALUE=? WHERE ID=?";
    private static final String DELETE = "DELETE FROM QUERY_STRINGS WHERE ID=?";

    @Override
    public QueryString create(QueryString queryString) throws DAOException {
        return null;
    }


    @Override
    public QueryString read(Integer id) throws DAOException {

        QueryString queryString = new QueryString();

        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(SELECT);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                queryString.setId(id);
                queryString.setQuery_key( rs.getString("QUERY_KEY") );
                queryString.setQuery_value( rs.getString("QUERY_VALUE") );
            }

            rs.close();
            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO GET ERROR: " + e.getMessage());
        }
        return queryString;
    }

    @Override
    public List<QueryString> readAll() throws DAOException {

        List<QueryString> queryStringList = new LinkedList<>();
        try (Connection connection = getConnection()) {

            Statement s = connection.createStatement();

            ResultSet rs = s.executeQuery(SELECT_ALL);

            while (rs.next()) {
                QueryString queryString = new QueryString();
                queryString.setId( rs.getInt("ID") );
                queryString.setQuery_key( rs.getString("QUERY_KEY") );
                queryString.setQuery_value( rs.getString("QUERY_VALUE") );
                queryStringList.add(queryString);
            }

            s.close();
            rs.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO GET ALL ERROR: " + e.getMessage());
        }
        return queryStringList;
    }

    @Override
    public QueryString update(QueryString queryString) throws DAOException {

        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(UPDATE);
            ps.setString(1, queryString.getQuery_key());
            ps.setString(2, queryString.getQuery_value());
            ps.setInt(3, queryString.getId());

            ps.executeUpdate();

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO UPDATE ERROR: " + e.getMessage());
        }

        return queryString;
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
            throw new DAOException("QueryStringDAO DELETE ERROR: " + e.getMessage());
        }

    }

    public QueryString createByRequestId(QueryString queryString, Integer requestId) throws DAOException {
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, queryString.getQuery_key());
            ps.setString(2, queryString.getQuery_value());
            ps.setInt(3, requestId);

            ps.executeUpdate();

            ResultSet generatedId = ps.getGeneratedKeys();
            if (generatedId.next()) {
                int queryStringId = generatedId.getInt(1);
                queryString.setId(queryStringId); // Set generated id
            }

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO SAVE BY REQUEST_ID ERROR: " + e.getMessage());
        }

        return queryString;
    }


    public void deleteByRequestId(Integer requestId) throws DAOException {

        try (Connection connection = getConnection()) {

            Statement s = connection.createStatement();

            s.executeUpdate("DELETE FROM QUERY_STRINGS WHERE REQUEST_ID=" + requestId);

            s.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO DELETE BY REQUEST_ID ERROR: " + e.getMessage());
        }

    }


    public List<QueryString> getAllByRequestId(Integer requestId) throws DAOException {

        List<QueryString> queryStringList = new LinkedList<>();
        try (Connection connection = getConnection()) {

            Statement s = connection.createStatement();

            ResultSet rs = s.executeQuery("SELECT * FROM QUERY_STRINGS WHERE REQUEST_ID=" + requestId);

            while (rs.next()) {
                QueryString queryString = new QueryString();
                queryString.setId( rs.getInt("ID") );
                queryString.setQuery_key( rs.getString("QUERY_KEY") );
                queryString.setQuery_value( rs.getString("QUERY_VALUE") );
                queryStringList.add(queryString);
            }

            s.close();
            rs.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("QueryStringDAO GET ALL BY REQUEST_ID ERROR: " + e.getMessage());
        }
        return queryStringList;
    }


}
