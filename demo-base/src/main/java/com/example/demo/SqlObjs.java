package com.example.demo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlObjs {
    private static final Map<String, String> operationTypeMethodMap = new HashMap<String, String>() {{
        put("add", "toIdempotenceInsertSql");
        put("new", "toIdempotenceInsertSql");
        put("delete", "toDeleteSql");
        put("update", "toUpdateSql");
        put("modify", "toUpdateSql");
    }};

    public static String toIdempotenceInsertSql(List<SqlObj> sqlObjList) {
        return sqlObjList.stream().map(SqlObj::toIdempotenceInsertSql).collect(Collectors.joining("\n\n"));
    }

    public static String toDeleteSql(List<SqlObj> sqlObjList) {
        return sqlObjList.stream().map(SqlObj::toDeleteSql).collect(Collectors.joining("\n\n"));
    }

    public static String toUpdateSql(List<SqlObj> sqlObjList) {
        return sqlObjList.stream().map(SqlObj::toUpdateSql).collect(Collectors.joining("\n\n"));
    }

    public static String toSqlByOperationType(List<SqlObj> sqlObjList){
        StringBuilder sb = new StringBuilder();
        for (SqlObj sqlObj:sqlObjList) {
            Field[] declaredFields = sqlObj.getClass().getDeclaredFields();
            for (Field field:declaredFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(SqlType.class)){
                    try {
                        String methodName = operationTypeMethodMap.get(field.get(sqlObj));
                        sb.append((String) sqlObj.getClass().getMethod(methodName).invoke(sqlObj));
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sb.toString();
    }
}
