package com.h3w.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;

public class JSONUtil {
    /**
     * @param jsonStr
     * @param obj
     * @return
     */
    public static <T> Object JSONToObj(String jsonStr, Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(jsonStr,
                    obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * @param obj
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static <T> JSONObject objectToJson(T obj) throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Convert object to JSON string  
        String jsonStr = "";
        try {
            jsonStr = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw e;
        }
        return new JSONObject(jsonStr);
    }

    /**
     * @param data
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static <T> JSONArray objectCollectionToJson(Collection<T> data) throws JSONException, IOException {
        JSONArray arr = new JSONArray();
        if (data != null)
            for (T obj : data) {
                arr.put(objectToJson(obj));
            }
        return arr;
    }

//     
//    public static void main(String[] args) throws JSONException, IOException {
//    	User user = new User();
//    	user.setId(1);
//    	user.setUsername("abc");
//    	user.setCreatetime(new Date());
//    	System.out.println(JSONUtil.objectToJson(user));
//    }
}
