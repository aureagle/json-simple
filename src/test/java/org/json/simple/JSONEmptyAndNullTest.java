package org.json.simple;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.Assert;
import org.junit.Test;
import junit.framework.TestCase;

public class JSONEmptyAndNullTest extends TestCase {
    
    public void testNullValue() throws ParseException  {
        String str = "null";
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        Object sol = null;
        Assert.assertEquals("null value", sol,  obj);
    }
    
    public void testNullValueInObject() throws ParseException  {
        String str = "{\"exception\":null,\"status\":\"ERROR\"}";
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        JSONObject sol = new JSONObject();
        sol.put("exception", null);
        sol.put("status", "ERROR");
        Assert.assertEquals("null value in JSON object", sol,  obj);
    }
    
    public void testNullValueInArray() throws ParseException {
        String str = "{\"list\": [null,\"hello\", null, \"world\"]}";
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        
        JSONArray array = new JSONArray();
        array.add(null);
        array.add("hello");
        array.add(null);
        array.add("world");
        JSONObject sol = new JSONObject();
        sol.put("list", array);
        
        Assert.assertEquals("null value in JSON array", sol,  obj);
    }
    
    public void testEmptyArray() throws ParseException {
        JSONParser parser = new JSONParser();
        String arrayStr = "[]";
        JSONArray array = (JSONArray)(parser.parse(arrayStr));
        JSONArray sol = new JSONArray();
        Assert.assertEquals("Empty array", sol, array );
    }
    
    public void testEmptyObject() throws ParseException {
        JSONParser parser = new JSONParser();
        String objStr = "{}";
        JSONObject obj = (JSONObject)(parser.parse(objStr));
        JSONObject sol = new JSONObject();
        Assert.assertEquals("Empty object", sol, obj );
    }
    
    public void testEmptyString() throws ParseException {
        JSONParser parser = new JSONParser();
        String objStr = "";
        Object obj = parser.parse(objStr);
        JSONObject sol = null;
        Assert.assertEquals("Empty object", sol, obj );
    }

}
