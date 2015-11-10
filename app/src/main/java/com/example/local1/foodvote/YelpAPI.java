package com.example.local1.foodvote;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import android.os.AsyncTask;
import android.util.Log;
import com.beust.jcommander.Parameter;

import android.widget.TextView;
import android.app.Activity;
import com.beust.jcommander.JCommander;
import java.net.URL;

/**
 * Code sample for accessing the Yelp API V2.
 *
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 *
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 *
 */
public class YelpAPI extends AsyncTask<String, Void, String> {

    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "dinner";
    private static final String DEFAULT_LOCATION = "San Francisco, CA";
    private static final int SEARCH_LIMIT = 10;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "FQFe1MpY3PGvxKy-Aq702g";
    private static final String CONSUMER_SECRET = "u_ifYEaonk6W5sf24SCXiGPKx6I";
    private static final String TOKEN = "6YSX1I448VpE2WQ1rrQv0NJRNJ9E9rOX";
    private static final String TOKEN_SECRET = "_iN4GhZsdgojn1WYZTHOi-q2jzM";

    OAuthService service;
    Token accessToken;

    String [] results;
    String res = "TOMATO";

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
        results = new String [5];
    }

    /* USE ASYNCTASK To run the API call on a non-UI/main thread. API Calls do not work on the main thread */

    /* Before Execution. As of now this isn't doing anything useful */
    @Override
    protected void onPreExecute(){
        Log.println(Log.ERROR, "YELPAPI:", "IN OnPreExecute!");
        res = "GARFIELD";
        //try{ Thread.sleep(10000); }catch(InterruptedException e){ }
    }


    /* The actual API Call process */
    @Override
    protected String doInBackground(String...Locations){

        Log.println(Log.ERROR, "YELPAPI:", "IN doInBackground!");

        /* Search for Buisnesses in the passed city parameter */
        String APIRes = searchForBusinessesByLocation("restuarant", Locations[0]);
        Log.println(Log.ERROR, "YELPAPI:", "Search Passed!");
        /*
        Activity.runOnUiThread(new Runnable() {
            public void run() {
                results[0] = APIRes;
            }
        });
        */

        //results[0] = APIRes;
        //return APIRes;
        Log.println(Log.ERROR, "YELPAPI:", "Result = " + APIRes);

        /* Return the JSON result of the API Call */
        return APIRes;
    }


    /* After the Execution. As of now not doing anything useful */
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        Log.println(Log.ERROR, "YELPAPI:", "IN OnPostExec!");

        //TextView tv1 = (TextView)findViewById(R.id.TextView1);
        //tv1.setText(result);
        res = result;

        //return result;
    }


    /* NOT BEING USED */
    public String getResults(){
        return res;
    }


    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term <tt>String</tt> of the search term to be queried
     * @param location <tt>String</tt> of the location
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String term, String location) {
        Log.println(Log.ERROR, "YELPAPI:", "IN searchForBuisnessByLocation!");
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        Log.println(Log.ERROR, "YELPAPI:", "Made OAuthRequest!");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", location);
        //request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        Log.println(Log.ERROR, "YELPAPI:", "Passed searchForBuisnessByLocation!");
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and sends a request to the Business API by business ID.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
        return request;
    }


    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     * f
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        Log.println(Log.ERROR, "YELPAPI:", "IN sendRequestAndGetResponse!");
        System.out.println("Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Log.println(Log.ERROR, "YELPAPI:", "Trying to send request!");
        Response response = request.send();
        Log.println(Log.ERROR, "YELPAPI:", "Returning from sendRequestAndGetResponse!");
        return response.getBody();
    }


    /**
     * Queries the Search API based on the command line arguments and takes the first result to query
     * the Business API.
     *
     * @param yelpApi <tt>YelpAPI</tt> service instance
     * @param yelpApiCli <tt>YelpAPICLI</tt> command line arguments
     */
    private static void queryAPI(YelpAPI yelpApi, YelpAPICLI yelpApiCli) {
        String searchResponseJSON =
                yelpApi.searchForBusinessesByLocation(yelpApiCli.term, yelpApiCli.location);

        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(searchResponseJSON);
        } catch (ParseException pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }

        JSONArray businesses = (JSONArray) response.get("businesses");
        JSONObject firstBusiness = (JSONObject) businesses.get(0);
        String firstBusinessID = firstBusiness.get("id").toString();
        System.out.println(String.format(
                "%s businesses found, querying business info for the top result \"%s\" ...",
                businesses.size(), firstBusinessID));

        // Select the first business and display business details
        String businessResponseJSON = yelpApi.searchByBusinessId(firstBusinessID.toString());
        System.out.println(String.format("Result for business \"%s\" found:", firstBusinessID));
        System.out.println(businessResponseJSON);
    }


    /**
     * Command-line interface for the sample Yelp API runner.
     */
    private static class YelpAPICLI {
        @Parameter(names = {"-q", "--term"}, description = "Search Query Term")
        public String term = DEFAULT_TERM;

        @Parameter(names = {"-l", "--location"}, description = "Location to be Queried")
        public String location = DEFAULT_LOCATION;
    }

    /*
    public String execute(YelpAPI yelpApi){
        YelpAPICLI yelpApiCli = new YelpAPICLI();
        new JCommander(yelpApiCli, args);

        YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        queryAPI(yelpApi, yelpApiCli);

        return yelpApi.searchForBusinessesByLocation("restaurant", "San Francisco, CA");
    }
    */
}