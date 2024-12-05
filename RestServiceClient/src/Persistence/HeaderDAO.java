package Persistence;

import Entity.Header;
import Exce.DAOException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class HeaderDAO extends DAOManager<Header> {
    private static final String INSERT = "INSERT INTO HEADER (HEADER_KEY, HEADER_VALUE) VALUES (?,?)";
    private static final String SELECT = "SELECT * FROM HEADER WHERE ID=?";
    private static final String SELECT_ALL = "SELECT * FROM HEADER";
    private static final String UPDATE = "UPDATE HEADER SET HEADER_KEY=?, HEADER_VALUE=? WHERE ID=?";
    private static final String DELETE = "DELETE FROM HEADER WHERE ID=?";

    @Override
    public Header create(Header header) throws DAOException {

        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, header.getHeader_key());
            ps.setString(2, header.getHeader_value());

            ps.executeUpdate();

            ResultSet generatedId = ps.getGeneratedKeys();
            if (generatedId.next()) {
                int headerId = generatedId.getInt(1);
                header.setId(headerId); // Set generated id
            }

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("HeaderDAO SAVE ERROR: " + e.getMessage());
        }

        return header;
    }

    @Override
    public Header read(Integer id) throws DAOException {

        Header header = new Header();

        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(SELECT);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                header.setId(id);
                header.setHeader_key( rs.getString("HEADER_KEY") );
                header.setHeader_value( rs.getString("HEADER_VALUE") );
            }

            rs.close();
            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("HeaderDAO GET ERROR: " + e.getMessage());
        }
        return header;
    }

    @Override
    public List<Header> readAll() throws DAOException {

        List<Header> headerList = new LinkedList<>();
        try (Connection connection = getConnection()) {

            Statement stat = connection.createStatement();

            ResultSet resultSet = stat.executeQuery(SELECT_ALL);

            while (resultSet.next()) {
                Header header = new Header();
                header.setId(resultSet.getInt("ID"));
                header.setHeader_key(resultSet.getString("HEADER_KEY"));
                header.setHeader_value(resultSet.getString("HEADER_VALUE"));
                headerList.add(header);
            }

            stat.close();
            resultSet.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("HeaderDAO GET ALL ERROR: " + e.getMessage());
        }
        return headerList;
    }

    @Override
    public Header update(Header header) throws DAOException {

        try (Connection connection = getConnection()) {

            PreparedStatement ps = connection.prepareStatement(UPDATE);
            ps.setString(1, header.getHeader_key());
            ps.setString(2, header.getHeader_value());
            ps.setInt(3, header.getId());

            ps.executeUpdate();

            ps.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("HeaderDAO UPDATE ERROR: " + e.getMessage());
        }

        return header;
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
            throw new DAOException("HeaderDAO DELETE ERROR: " + e.getMessage());
        }
    }

    public void cleanUnusedHeaders() throws DAOException {
        try (Connection connection = getConnection()) {

            Statement statement = connection.createStatement();

            statement.executeUpdate("DELETE FROM HEADER WHERE ID NOT IN ( SELECT HEADER_ID FROM REQUEST WHERE HEADER_ID IS NOT NULL )");

            statement.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new DAOException("HeaderDAO CLEAN ERROR: " + e.getMessage());
        }

    }
}
