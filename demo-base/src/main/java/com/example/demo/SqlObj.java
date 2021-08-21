package com.example.demo;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SqlObj {
    private static List<String> keyWordList = Arrays.asList("key","value");

    public String toIdempotenceInsertSql(){
        return this.toDeleteSql()+"\n\n"+this.toInserSql();
    }
    
    public String toInserSql() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = Objects.isNull(sqlTableAnnotation)?null:sqlTableAnnotation.value();
        tableName = processDefaultName(itemClass.getName().substring(itemClass.getName().lastIndexOf(".")+1),tableName,true);
        tableName = processKeyWord(tableName);
        Field[] declaredFields = itemClass.getDeclaredFields();
        sb.append(tableName+"(");
        for (Field field:declaredFields){
            field.setAccessible(true);
            SqlValue sqlValueAnnotation = field.getAnnotation(SqlValue.class);
            if (Objects.isNull(sqlValueAnnotation)){
                continue;
            }
            String columnName = sqlValueAnnotation.value();
            columnName = processColumnName(field, columnName);
            sb.append(columnName+",");
        }
        sb.delete(sb.length()-1,sb.length());
        sb.append(") VALUES(");
        for (Field field:declaredFields){
            field.setAccessible(true);
            SqlValue sqlValueAnnotation = field.getAnnotation(SqlValue.class);
            if (Objects.isNull(sqlValueAnnotation)){
                continue;
            }
            Object value = getValueStringWithType(field);
            sb.append(value+",");
        }
        sb.delete(sb.length()-1,sb.length());
        sb.append(");");
        return sb.toString();
    }

    public String toDeleteSql() {
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = Objects.isNull(sqlTableAnnotation)?null:sqlTableAnnotation.value();
        tableName = processDefaultName(itemClass.getName().substring(itemClass.getName().lastIndexOf(".")+1),tableName,true);
        tableName = processKeyWord(tableName);
        Field[] declaredFields = itemClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(tableName+" WHERE ");
        processWhere(sb, declaredFields);
        sb.append(";");
        return sb.toString();
    }

    public String toUpdateSql() {
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = Objects.isNull(sqlTableAnnotation)?null:sqlTableAnnotation.value();
        tableName = processDefaultName(itemClass.getName().substring(itemClass.getName().lastIndexOf(".")+1),tableName,true);
        tableName = processKeyWord(tableName);
        Field[] declaredFields = itemClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(tableName+" SET ");
        processSet(sb, declaredFields);
        sb.append("WHERE ");
        processWhere(sb, declaredFields);
        sb.append(";");
        return sb.toString();
    }

    private String processColumnName(Field field, String columnName) {
        columnName = processDefaultName(field.getName(), columnName);
        columnName = processKeyWord(columnName);
        return columnName;
    }

    private static String processDefaultName(String defaultName, String name) {
        return processDefaultName(defaultName,name,false);
    }

    private static String processDefaultName(String defaultName, String name, boolean isToLowercase) {
        if (StringUtils.isBlank(name)) {
            name = defaultName;
            if (isToLowercase){
                name = name.toLowerCase();
            }
        }
        return name;
    }

    private static String processKeyWord(String name) {
        final String fColumnName = name;
        if (keyWordList.parallelStream().filter(keyWord -> keyWord.equalsIgnoreCase(fColumnName)).count() > 0) {
            name = "`" + name + "`";
        }
        return name;
    }

    private void processSet(StringBuilder sb, Field[] declaredFields) {
        for (Field field:declaredFields){
            field.setAccessible(true);
            SqlSet valueAnnotation = field.getAnnotation(SqlSet.class);
            if (Objects.isNull(valueAnnotation)){
                continue;
            }
            String columnName = valueAnnotation.value();
            columnName = processColumnName(field, columnName);
            Object value = getValueStringWithType(field);
            sb.append(columnName+" = " + value + " AND ");
        }
        sb.delete(sb.length()-4,sb.length());
    }

    private void processWhere(StringBuilder sb, Field[] declaredFields) {
        for (Field field : declaredFields) {
            field.setAccessible(true);
            SqlWhere valueAnnotation = field.getAnnotation(SqlWhere.class);
            if (Objects.isNull(valueAnnotation)) {
                continue;
            }
            String columnName = valueAnnotation.value();
            columnName = processColumnName(field, columnName);
            Object value = getValueStringWithType(field);
            sb.append(columnName + " = " + value + " AND ");
        }
        sb.delete(sb.length() - 4, sb.length());
    }

    private Object getValueStringWithType(Field field) {
        Object value = null;
        try {
            value = field.get(this);
            if (field.getGenericType().toString().equals("class java.lang.String")) {
                value = "'" + field.get(this) + "'";
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
}
