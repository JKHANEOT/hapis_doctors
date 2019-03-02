package com.hapis.customer.ui.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum  UserTypeEnum {

    AGENT(Integer.valueOf(1), "AGENT"),

    USER(Integer.valueOf(2), "USER"),

    DOCTOR(Integer.valueOf(3), "DOCTOR");

    private Integer code;

    private String value;


    private static Map<Integer, String> values;


    private static Map<String, Integer> codes;

    private UserTypeEnum(Integer code, String value)
    {
        this.code = code;
        this.value = value;
    }

    static
    {
        values = new HashMap<>();


        codes = new HashMap<>();

        for (UserTypeEnum userTypes : values()) {
            codes.put(userTypes.value(), userTypes.code());
            values.put(userTypes.code(), userTypes.value());
        }
    }

    public Integer code()
    {
        return code;
    }

    public String value()
    {
        return value;
    }

    public Integer getCode(String value)
    {
        return (Integer)codes.get(value);
    }

    public String getValue(Integer code)
    {
        return (String)values.get(code);
    }
}