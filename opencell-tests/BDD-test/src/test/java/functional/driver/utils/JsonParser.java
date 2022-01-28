package functional.driver.utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonParser {

    private List<String> pathList;
    private String json;

    public JsonParser(String json) {
        this.json = json;
        this.pathList = new ArrayList<>();
        setJsonPaths(json);
    }

    public List<String> getPathList() {
        return this.pathList;
    }

    private void setJsonPaths(String json) {
        this.pathList = new ArrayList<String>();
        JSONObject object = new JSONObject(json);
        String jsonPath = "$";
        if(json != JSONObject.NULL) {
            readObject(object, jsonPath);
        }
    }

    private void readObject(JSONObject object, String jsonPath) {
        Iterator<String> keysItr = object.keys();
        String parentPath = jsonPath;
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            jsonPath = parentPath + "." + key;

            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            }
            else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);
            }
        }
    }

    private void readArray(JSONArray array, String jsonPath) {
        String parentPath = jsonPath;
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            jsonPath = parentPath + "[" + i + "]";

            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath);
            } else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath);
            } else { // is a value
                this.pathList.add(jsonPath);
            }
        }
    }

}
