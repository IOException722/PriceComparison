package parser;

import android.util.Log;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by abhay on 14/7/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResultParser {
    public static ObjectMapper objectMapper;
    public JsonResultParser()
    {
        if(objectMapper==null){
            objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }
    public Object getJsonMap(String jsonString, int type)
    {
        Object objOutput = null;
        try{
            switch (type)
            {
                case 4:
                    Log.v("Ascii","obj out1 ");

                    objOutput= objectMapper.readValue(jsonString, SearchResultParser.class);
                    Log.v("Ascii","obj out ");
                    Log.v("Ascii",""+objOutput.toString());
                    break;
                case 3:
                    Log.v("Ascii","obj out1 ");

                    objOutput= objectMapper.readValue(jsonString, ProductDetails.class);
                    Log.v("Ascii","obj out ");
                    Log.v("Ascii",""+objOutput.toString());
                    break;
                case 1:
                    Log.v("Ascii","obj out1 ");
                    if(objectMapper==null)
                        Log.v("Ascii","its null");
                    objOutput= objectMapper.readValue(jsonString, Categories.class);
                    Log.v("Ascii","obj out ");
                    Log.v("Ascii",""+objOutput.toString());
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return objOutput;
    }
}
