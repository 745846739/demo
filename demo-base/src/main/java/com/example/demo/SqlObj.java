package com.example.demo;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class SqlObj {
    private static List<String> keyWordList = Arrays.asList("key","value");

    public String toIdempotenceInsertSql(){
        return this.toDeleteSql()+this.toInserSql();
    }
    
    public String toInserSql() {
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = processTableName(itemClass, sqlTableAnnotation);
        Field[] declaredFields = itemClass.getDeclaredFields();

        StringBuilder sb = new StringBuilder("INSERT INTO ");
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
        sb.append(");\n\n");
        return sb.toString();
    }

    public String toDeleteSql() {
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = processTableName(itemClass, sqlTableAnnotation);
        Field[] declaredFields = itemClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(tableName+" WHERE ");
        processWhere(sb, declaredFields);
        sb.append(";\n\n");
        return sb.toString();
    }

    public String toUpdateSql() {
        Class<? extends SqlObj> itemClass = this.getClass();
        SqlTable sqlTableAnnotation = itemClass.getAnnotation(SqlTable.class);
        String tableName = processTableName(itemClass, sqlTableAnnotation);
        Field[] declaredFields = itemClass.getDeclaredFields();
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(tableName+" SET ");
        processSet(sb, declaredFields);
        sb.append(" WHERE ");
        processWhere(sb, declaredFields);
        sb.append(";\n\n");
        return sb.toString();
    }

    private String processTableName(Class<? extends SqlObj> itemClass, SqlTable sqlTableAnnotation) {
        String tableName = Objects.isNull(sqlTableAnnotation) ? null : sqlTableAnnotation.value();
        tableName = processDefaultName(itemClass.getName().substring(itemClass.getName().lastIndexOf(".") + 1), tableName, true);
        tableName = processKeyWord(tableName);
        return tableName;
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
        sb.delete(sb.length()-5,sb.length());
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
        sb.delete(sb.length() - 5, sb.length());
    }

    private Object getValueStringWithType(Field field) {
        Object value = null;
        try {
            value = field.get(this);
            if (field.getType().equals(String.class)) {
                value = "'" + field.get(this) + "'";
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    static class SqlObjComparator implements Comparator<SqlObj>{
        @Override
        public int compare(SqlObj o1, SqlObj o2) {
            Field[] declaredFields = o1.getClass().getDeclaredFields();
            for (Field field:declaredFields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SqlOrder.class)){
                    continue;
                }
                if (field.getType().equals(Date.class)){
                    try {
                        return ((Date)field.get(o1)).after((Date)field.get(o2))?1:-1;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return 0;
            }
            return 0;
        }
    }
}
