package com.example.demo;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        ApolloItem item = ApolloItem.builder().id(1L).name("test").key("testKey").value("testValue").namespaceId(1L).approvalTime(new Date(333333)).itemOperationType("delete").build();
        ApolloItem item1 = ApolloItem.builder().id(2L).name("test2").key("testKey2").value("testValue2").namespaceId(2L).approvalTime(new Date(222222)).itemOperationType("add").build();
        ApolloItem item2 = ApolloItem.builder().id(3L).name("test3").key("testKey3").value("testValue3").namespaceId(3L).approvalTime(new Date(111111)).itemOperationType("update").build();
        List<SqlObj> itemList = Arrays.asList(item, item1, item2);
        itemList.sort(new SqlObj.SqlObjComparator());
        String sql = SqlObjs.toSqlByOperationType(itemList);
        System.out.println(sql);
//        try {
//            boolean b = ApolloItem.class.getDeclaredField("namespaceId").isAnnotationPresent(SqlUnderline.class);
//            System.out.println(b);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
    }
}
