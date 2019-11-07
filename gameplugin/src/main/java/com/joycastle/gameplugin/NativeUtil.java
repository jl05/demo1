package com.joycastle.gameplugin;

import com.joycastle.gamepluginbase.InvokeJavaMethodDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class NativeUtil {
    private static final String TAG = "NativeUtil";

    /**
     *  C++调用Java函数
     * @param className 类名
     * @param methodName 函数名
     * @param reqJson json数据
     * @param requestId requestId
     * @return json数据
     */
    public static String invokeJavaMethod(String className, String methodName, String reqJson, final int requestId) {
        String resJson = "";
        try {
            ArrayList<Object> reqArrayList = parseJson(reqJson);

            int argsNum = reqArrayList.size() + (requestId < 0 ? 0 : 1);
            Class[] classArr = new Class[argsNum];
            Object[] objectArr = new Object[argsNum];
            for (int i=0; i<reqArrayList.size(); i++) {
                Object obj = reqArrayList.get(i);
                if(obj.getClass() == Integer.class){
                    classArr[i] = int.class;
                }else if (obj.getClass() == Float.class){
                    classArr[i] = float.class;
                }else if (obj.getClass() == Double.class) {
                    classArr[i] = double.class;
                }else if (obj.getClass() == Boolean.class) {
                    classArr[i] = boolean.class;
                }else{
                    classArr[i] = obj.getClass();
                }
                objectArr[i] = obj;
            }
            if (requestId >= 0) {
                classArr[argsNum-1] = InvokeJavaMethodDelegate.class;
                objectArr[argsNum-1] = new InvokeJavaMethodDelegate() {
                    @Override
                    public void onFinish(ArrayList<Object> resArrayList) {
                        try {
                            invokeCppMethod(requestId, generateJson(resArrayList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            Class clazz = Class.forName(className);
            Method getInstanceMethod = clazz.getMethod("getInstance");
            Object instance = getInstanceMethod.invoke(null);
            Method targetMethod = clazz.getMethod(methodName, classArr);
            Object ret = targetMethod.invoke(instance, objectArr);
            ArrayList<Object> resArrayList = new ArrayList<>();
            resArrayList.add(ret);
            resJson = generateJson(resArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resJson;
    }

    /**
     * 根据参数列表生成json
     * @param arrayList 参数列表
     * @return json
     * @throws JSONException 异常
     */
    private static String generateJson(ArrayList<Object> arrayList) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = arrayList2JsonArray(arrayList);
        jsonObject.put("value", jsonArray);
        return jsonObject.toString();
    }

    /**
     * 解析json
     * @param jsonStr json
     * @return 参数列表
     * @throws JSONException 异常
     */
    private static ArrayList<Object> parseJson(String jsonStr) throws JSONException {
        JSONObject reqObj = new JSONObject(jsonStr);
        JSONArray valArr = reqObj.getJSONArray("value");
        JSONArray typeArr = reqObj.getJSONArray("type");
        return jsonArray2ArrayList(valArr, typeArr);
    }

    private static ArrayList<Object> jsonArray2ArrayList(JSONArray valArr, JSONArray typeArr) throws JSONException {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int i = 0; i < typeArr.length(); i++) {
            if (typeArr.optString(i).equals("int")) {
                arrayList.add(valArr.getInt(i));
            } else if (typeArr.optString(i).equals("float")) {
                arrayList.add(valArr.getDouble(i));
            } else if (typeArr.optString(i).equals("double")) {
                arrayList.add(valArr.getDouble(i));
            } else if (typeArr.optString(i).equals("bool")) {
                arrayList.add(valArr.getBoolean(i));
            } else if (typeArr.optString(i).equals("string")) {
                arrayList.add(valArr.getString(i));
            } else if (typeArr.optJSONArray(i) != null) {
                arrayList.add(jsonArray2ArrayList(valArr.getJSONArray(i), typeArr.getJSONArray(i)));
            } else if (typeArr.optJSONObject(i) != null) {
                arrayList.add(jsonObject2HashMap(valArr.getJSONObject(i), typeArr.getJSONObject(i)));
            }
        }
        return arrayList;
    }

    private static HashMap<String, Object> jsonObject2HashMap(JSONObject valObj, JSONObject typeObj) throws JSONException {
        HashMap<String, Object> hashMap = new HashMap<>();
        Iterator<String> keys = valObj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (typeObj.optString(key).equals("int")) {
                hashMap.put(key, valObj.getInt(key));
            }  else if (typeObj.optString(key).equals("float")) {
                hashMap.put(key, valObj.getDouble(key));
            } else if (typeObj.optString(key).equals("double")) {
                hashMap.put(key, valObj.getDouble(key));
            } else if (typeObj.optString(key).equals("bool")) {
                hashMap.put(key, valObj.getBoolean(key));
            } else if (typeObj.optString(key).equals("string")) {
                hashMap.put(key, valObj.getString(key));
            } else if (typeObj.optJSONArray(key) != null) {
                hashMap.put(key, jsonArray2ArrayList(valObj.getJSONArray(key), typeObj.getJSONArray(key)));
            } else if (typeObj.optJSONObject(key) != null) {
                hashMap.put(key, jsonObject2HashMap(valObj.getJSONObject(key), typeObj.getJSONObject(key)));
            }
        }
        return hashMap;
    }

    private static JSONArray arrayList2JsonArray(ArrayList arrayList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object val : arrayList) {
            if (val instanceof ArrayList) {
                jsonArray.put(arrayList2JsonArray((ArrayList) val));
            } else if (val instanceof HashMap) {
                jsonArray.put(hashMap2JsonObject((HashMap) val));
            } else {
                jsonArray.put(val);
            }
        }
        return jsonArray;
    }

    private static JSONObject hashMap2JsonObject(HashMap hashMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Iterator it = hashMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            Object val = hashMap.get(key);
            if (val instanceof ArrayList) {
                jsonObject.put(key, arrayList2JsonArray((ArrayList) val));
            } else if (val instanceof HashMap) {
                jsonObject.put(key, hashMap2JsonObject((HashMap) val));
            } else {
                jsonObject.put(key, val);
            }
        }
        return jsonObject;
    }

    /**
     * Java调用C++函数
     * @param responseId 对应invokeJavaMethod的requestId
     * @param resData json数据
     */
    public static native void invokeCppMethod(int responseId, String resData);
}
