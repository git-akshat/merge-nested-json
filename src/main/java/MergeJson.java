import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MergeJson {

    private static JSONObject deepMerge(JSONObject original, JSONObject newJSon) {
        for (Object key : newJSon.keySet()) {

            if (newJSon.get(key) instanceof JSONObject && original.get(key) instanceof JSONObject) {
                JSONObject originalChild = (JSONObject) original.get(key);
                JSONObject newChild = (JSONObject) newJSon.get(key);
                original.put(key, deepMerge(originalChild, newChild));

            } else if (newJSon.get(key) instanceof JSONArray && original.get(key) instanceof JSONArray) {
                JSONArray originalChild = (JSONArray) original.get(key);
                JSONArray newChild = (JSONArray) newJSon.get(key);

                for (Object each : newChild) {
                    if (each instanceof JSONObject) {
                        int small = Math.min(originalChild.size(), newChild.size());
                        int i=0;
                        for (i=0; i<small; i++) {
                            JSONParser jsonParser = new JSONParser();
                            JSONObject oChild = null;
                            JSONObject nChild = null;
                            try {
                                if ((JSONObject) (jsonParser.parse(originalChild.get(i).toString())) != null && (JSONObject) (jsonParser.parse(newChild.get(i).toString())) != null) {
                                    oChild = (JSONObject) (jsonParser.parse(originalChild.get(i).toString()));
                                    nChild = (JSONObject) (jsonParser.parse(newChild.get(i).toString()));
                                    JSONObject obj = (deepMerge(oChild, nChild));
                                    originalChild.set(i, obj);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        while(i < newChild.size()) {
                            originalChild.add(newChild.get(i));
                            i++;
                        }
                    } else if (!originalChild.contains(each)) {
                        originalChild.add(each);
                    }
                }
            } else {
                original.put(key, newJSon.get(key));
            }
        }
        return original;
    }

    private static JSONArray merge(JSONArray arr1, JSONArray arr2) {
        arr2.removeAll(arr1);
        arr1.addAll(arr2);
        return arr1;
    }

    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        JSONObject obj1 = null;
        JSONObject obj2 = null;
        JSONObject result = null;
        try {
            // read input from FILE
            obj1 = (JSONObject) jsonParser.parse(new FileReader("src\\main\\resources\\a.json"));
            obj2 = (JSONObject) jsonParser.parse(new FileReader("src\\main\\resources\\b.json"));
            result = deepMerge(obj1, obj2);
            // WRITE OUTPUT TO FILE
            FileOutputStream fos = new FileOutputStream("src\\main\\resources\\result.json");
            fos.write(result.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("obj1 = " + obj1);
        System.out.println("obj2 = " + obj2);
        System.out.println("result = " + result);


    }
}
