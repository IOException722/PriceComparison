package parser;

import java.util.ArrayList;

/**
 * Created by abhay on 14/7/16.
 */
public class ProductDetails {
    public String request_status;
    public RequestResult request_result;

    public static class RequestResult{
        public String id;
        public String category;
        public String name;
        public int price;
        public String brand;
        public String link;
        public String img_url;
        public ArrayList<Prices> prices;
    }
    public static class Prices
    {
        public String store_name;
        public String store_url;
        public String  store_rating;
        public String store_delivery;
        public String  name;
        public String link;
        public String price;
        public String stock;
        public String delivery;
        public String shipping_cost;
        public String pos;
        public String logo;
    }
}
