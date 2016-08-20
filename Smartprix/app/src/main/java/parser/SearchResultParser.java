package parser;

import java.util.ArrayList;

/**
 * Created by abhay on 14/7/16.
 */
public class SearchResultParser {
    public String request_status;
    public RequestResult request_result;

    public static class RequestResult{
        public ArrayList<Results> results;
    }
    public static class Results
    {
        public String id;
        public String category;
        public String name;
        public int price;
        public String brand;
        public String img_url;
    }
    public String results_count;
    public String results_count_total;
}
