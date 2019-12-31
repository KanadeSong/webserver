package com.seater.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonHelper {

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType)  throws IOException {
            if (jsonString == "null")
                return null;
            if (jsonString == "") {
                if (valueType != String.class)
                    return null;
                else
                    return (T) "";
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

            return mapper.readValue(jsonString, valueType);
    }

    public static String toJsonString(Object obj) throws JsonProcessingException
    {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
