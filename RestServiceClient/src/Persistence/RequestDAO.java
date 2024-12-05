package Persistence;

import Entity.Request;
import Exce.DAOException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class RequestDAO extends DAOManager<Request> {

    private static final String INSERT = "INSERT INTO REQUEST (NAME, ADDRESS, BODY, METHOD_ID, HEADER_ID) VALUES (?,?,?,?,?)";
    private static final String SELECT = "SELECT * FROM REQUEST WHERE ID=?";
    private static final String SELECT_ALL = "SELECT * FROM REQUEST";
    private static final String UPDATE = "UPDATE REQUEST SET NAME=?, ADDRESS=?, BODY=?, METHOD_ID=?, HEADER_ID=? WHERE ID=?";
    private static final String DELETE = "DELETE FROM REQUEST WHERE ID=?";

    private final HeaderDAO headerDAO;
    private final QueryStringDAO queryStringDAO;
    private final MethodDAO methodDAO;

    public RequestDAO(HeaderDAO headerDAO, QueryStringDAO queryStringDAO, MethodDAO methodDAO) {
        this.headerDAO = headerDAO;
        this.queryStringDAO = queryStringDAO;
        this.methodDAO = methodDAO;
    }


    @Override
    public Request create(Request request) throws DAOException {

        try (Connection connection = getConnection()) { // try with resources block closes resources in case of an exception

            // Prepare statement
            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getName());
            ps.setString(2, request.getAddress());
            ps.setString(3, request.getBody());
            ps.setInt(4, request.getMethod().getId());

            if ( request.getHeader() != null && request.getHeader().getId() != null )
                ps.setInt(5, request.getHeader().getId());
            else ps.setString(5, null);

            // Execute statement
            ps.executeUpdate();

            // Get auto increment generated ID
            ResultSet generatedId = ps.getGeneratedKeys();
            if (generatedId.next()) {
                int requestId = generatedId.getInt(1);
                request.setId(requestId); // Set id to Request
            }

            // Close processes
            ps.close();
            generatedId.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("RequestDAO SAVE ERROR: " + e.getMessage());
        }

        return request;
    }

    @Override
    public Request read(Integer id) throws DAOException {

        Request request = new Request();
        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(SELECT);
            ps.setInt(1, id);

            // Execute statement
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                request.setId(id);
                request.setName(rs.getString("NAME"));
                request.setAddress(rs.getString("ADDRESS"));
                request.setBody(rs.getString("BODY"));
                request.setMethod(methodDAO.read( rs.getInt("METHOD_ID") ));
                request.setHeader(headerDAO.read( rs.getInt("HEADER_ID") ));
                request.setQueryStrings(queryStringDAO.getAllByRequestId(id));
            }

            // Close processes
            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("RequestDAO GET ERROR: " + e.getMessage());
        }

        return request;
    }

    @Override
    public List<Request> readAll() throws DAOException {

        List<Request> requestList = new LinkedList<>();
        try (Connection connection = getConnection()) {

            // Create statement
            Statement stat = connection.createStatement();

            // Execute statement
            ResultSet resultSet = stat.executeQuery(SELECT_ALL);

            while (resultSet.next())
            {
                Request request = new Request();
                request.setId(resultSet.getInt("ID"));
                request.setName(resultSet.getString("NAME"));
                request.setAddress(resultSet.getString("ADDRESS"));
                request.setBody(resultSet.getString("BODY"));
                request.setMethod(methodDAO.read( resultSet.getInt("METHOD_ID") ));
                request.setHeader(headerDAO.read( resultSet.getInt("HEADER_ID") ));
                request.setQueryStrings(queryStringDAO.getAllByRequestId(resultSet.getInt("ID")));

                requestList.add(request);
            }

            // Close processes
            stat.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("RequestDAO GET ALL ERROR: " + e.getMessage());
        }
        return requestList;

    }

    @Override
    public Request update(Request request) throws DAOException {

        try (Connection connection = getConnection()) {

            // Prepare statement
            PreparedStatement prepStat = connection.prepareStatement(UPDATE);
            prepStat.setString(1, request.getName());
            prepStat.setString(2, request.getAddress());
            prepStat.setString(3, request.getBody());
            prepStat.setInt(4, request.getMethod().getId());

            if ( request.getHeader() != null && request.getHeader().getId() != null )
                prepStat.setInt(5, request.getHeader().getId());
            else prepStat.setString(5, null);

            prepStat.setInt(6, request.getId());

            // Execute statement
            prepStat.executeUpdate();

            // Close processes
            prepStat.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("RequestDAO UPDATE ERROR: " + e.getMessage());
        }
        return request;
    }

    @Override
    public void delete(Integer id) throws DAOException {

        try (Connection connection = getConnection()) {

            // Prepare statement
            PreparedStatement ps = connection.prepareStatement(DELETE);
            ps.setInt(1, id);

            // Execute statement
            ps.executeUpdate();

            // Close processes
            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("RequestDAO DELETE ERROR: " + e.getMessage());
        }
    }
}