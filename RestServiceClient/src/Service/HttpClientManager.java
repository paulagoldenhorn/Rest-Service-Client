package Service;

import Entity.QueryString;
import Entity.Request;
import Exce.HttpException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class HttpClientManager {

    private String buildUri(Request r) {
        StringBuilder uri = new StringBuilder( r.getAddress() );
        if ( r.getQueryStrings() != null )
        {
            uri.append("?");
            for ( QueryString qs : r.getQueryStrings() )
            {
                uri.append( qs.getQuery_key() )
                        .append("=")
                        .append( qs.getQuery_value() )
                        .append("&");
            }
            // Errase last "&"
            uri.setLength( uri.length() - 1 );
        }
        return uri.toString();
    }

    private HttpResponse<String> sendGet(Request r) throws HttpException {

        try {
            // Build URI
            String uri = buildUri(r);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri( new URI(uri) )
                    .GET();

            // Set headers
            if (r.getHeader() != null) {
                if ( r.getHeader().getHeader_key() != null && r.getHeader().getHeader_value() != null )
                    requestBuilder.headers( r.getHeader().getHeader_key(), r.getHeader().getHeader_value() );
            }

            // Send
            return getResponse( requestBuilder.build() );

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new HttpException("HttpClientManager SEND GET ERROR: " + e.getMessage());
        }

    }

    private HttpResponse<byte[]> sendImageGet(Request r) throws HttpException {

        try {
            // Build URI
            String uri = buildUri(r);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri( new URI(uri) )
                    .GET();

            // Set headers
            if (r.getHeader() != null) {
                if ( r.getHeader().getHeader_key() != null && r.getHeader().getHeader_value() != null )
                    requestBuilder.headers( r.getHeader().getHeader_key(), r.getHeader().getHeader_value() );
            }

            // Send
            return getResponseImage( requestBuilder.build() );

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new HttpException("HttpClientManager SEND IMAGE GET ERROR: " + e.getMessage());
        }

    }

    private HttpResponse<String> sendPost(Request r) throws HttpException {

        try {
            // Build URI
            String uri = buildUri(r);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri( new URI(uri) )
                    .POST( HttpRequest.BodyPublishers.ofString( r.getBody() ) );

            // Set headers
            if ( r.getHeader().getHeader_key() != null && r.getHeader().getHeader_value() != null )
                requestBuilder.headers( r.getHeader().getHeader_key(), r.getHeader().getHeader_value() );

            // Send
            return getResponse( requestBuilder.build() );

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new HttpException("HttpClientManager SEND POST ERROR: " + e.getMessage());
        }

    }

    private HttpResponse<String> sendPut(Request r) throws HttpException {

        try {
            // Build URI
            String uri = buildUri(r);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri( new URI(uri) )
                    .PUT( HttpRequest.BodyPublishers.ofString(r.getBody()) );

            // Set headers
            if ( r.getHeader().getHeader_key() != null && r.getHeader().getHeader_value() != null )
                requestBuilder.headers( r.getHeader().getHeader_key(), r.getHeader().getHeader_value() );

            // Send
            return getResponse( requestBuilder.build() );

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new HttpException("HttpClientManager SEND PUT ERROR: " + e.getMessage());
        }

    }

    private HttpResponse<String> sendDelete(Request r) throws HttpException {

        try {
            // Build URI
            String uri = buildUri(r);

            // Build request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri( new URI(uri) )
                    .DELETE();

            // Send
            return getResponse( requestBuilder.build() );

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new HttpException("HttpClientManager SEND DELETE ERROR: " + e.getMessage());
        }

    }

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS) // Set redirect policy in case of 3xx status code
                .build()
                .send(request, BodyHandlers.ofString()); // Response data type
    }

    private HttpResponse<byte[]> getResponseImage(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
                .send(request, BodyHandlers.ofByteArray()); // Response data type
    }

    public HttpResponse<String> executeRequest (Request r) {

        HttpResponse<String> response = null;
        try {

            switch ( r.getMethod().getType() ) {
                case "GET" -> response = sendGet(r);
                case "POST" -> response = sendPost(r);
                case "PUT" -> response = sendPut(r);
                case "DELETE" -> response = sendDelete(r);
            }

        } catch (HttpException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return response;
    }

    public HttpResponse<byte[]> executeImageRequest (Request r) {

        HttpResponse<byte[]> response;
        try {
            response = sendImageGet(r);
        } catch (HttpException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return response;
    }



}
