package Service;

import Entity.Method;
import Entity.QueryString;
import Entity.Request;
import Exce.DAOException;
import Persistence.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FavRequestsManager {
    private static final Logger LOGGER = Logger.getLogger("FavRequestsManager_Logger");
    private final RequestDAO requestDAO;
    private final HeaderDAO headerDAO;
    private final QueryStringDAO queryStringDAO;
    private final MethodDAO methodDAO;

    public FavRequestsManager() {
        headerDAO = new HeaderDAO();
        queryStringDAO = new QueryStringDAO();
        methodDAO = new MethodDAO();
        requestDAO = new RequestDAO(headerDAO, queryStringDAO, methodDAO);
    }

    public void addToFavs(Request r) {
        try {

            if ( r.getAddress() != null && !(r.getAddress().equals("")) ) {

                // Create header
                saveHeader(r);

                // Create request
                r = requestDAO.create(r);

                // Create query strings
                saveQueryStrings(r);

                LOGGER.info( "Request added to favs");

            } else LOGGER.log( Level.WARNING, "Request address can not be null" );

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFav(Request r) {
        try {

            if ( r.getId() != null ) {

                // Delete query strings
                queryStringDAO.deleteByRequestId( r.getId() );

                // Delete request
                requestDAO.delete( r.getId() );

                // Delete header
                if ( r.getHeader().getId() != null )
                    headerDAO.delete( r.getHeader().getId() );

                LOGGER.info("Request deleted from favs");

            } else LOGGER.log(Level.WARNING,"Id must not be null");

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modifyFav(Request r) {
        try {

            if ( r.getAddress() != null && !r.getAddress().equals("") ) {

                // Create header
                saveHeader(r);

                // Update request
                r = requestDAO.update(r);

                // Delete query strings
                queryStringDAO.deleteByRequestId(r.getId());

                // Create query strings
                saveQueryStrings(r);

                // Delete header
                headerDAO.cleanUnusedHeaders();

                LOGGER.info("Request modified from favs");

            } else LOGGER.log(Level.WARNING,"Missing Request address");

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public Request loadFav(Integer id)  {
        try {

            if (id != null)
                return requestDAO.read(id);

            else {
                LOGGER.log(Level.WARNING,"Id must not be null");
                return null;
            }

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Request> loadAllFavs() {
        try {

            return requestDAO.readAll();

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Method> loadAllMethods() {
        try {

            return methodDAO.readAll();

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public Method loadMethodByType(String type) {
        try {

            return methodDAO.readByType(type);

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveHeader(Request r) throws DAOException {
        if ( r.getHeader() != null )
        {
            if  (r.getHeader().getHeader_key() != null && r.getHeader().getHeader_value() != null )
                r.setHeader(headerDAO.create( r.getHeader() ));
        }

    }
    private void saveQueryStrings(Request r) throws DAOException {
        if ( r.getQueryStrings() != null )
        {
            for ( QueryString qs : r.getQueryStrings() )
            {
                if ( qs.getQuery_key() != null && qs.getQuery_value() != null )
                    qs.setId(queryStringDAO.createByRequestId( qs, r.getId() ).getId());
            }
        }
    }
}
