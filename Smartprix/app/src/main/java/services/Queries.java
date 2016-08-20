package services;

/**
 * Created by abhay on 14/7/16.
 */
public class Queries {
    public static  final String host="http://api.smartprix.com/simple/v1?type";
    public static final String api_key = "NVgien7bb7P5Gsc8DWqc";
    public enum Url {search,categories,product, product_full}

    public static String QueriesUrl(String type, String[] params)
    {
        Url url = Url.valueOf(type);
        switch (url)
        {
            case search:
                return host+"="+url+"&key="+api_key+"&category="+params[0]+"&q=3g&start="+params[1]+"indent=1";
            case categories:
                return host+"="+url+"&key="+api_key+"&indent=1";
            case product:
            case product_full:
                return host+"="+url+"&key="+api_key+"&id="+params[0]+"&indent=1";
            default:
                return null;

        }

    }

}
