package com.example.demo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        Item item = Item.builder().id(1L).name("test").key("testKey").value("testValue").namespaceId(1L).approvalTime(new Date(333333)).build();
        Item item1 = Item.builder().id(2L).name("test2").key("testKey2").value("testValue2").namespaceId(2L).approvalTime(new Date(222222)).build();
        Item item2 = Item.builder().id(3L).name("test3").key("testKey3").value("testValue3").namespaceId(3L).approvalTime(new Date(111111)).build();
        List<SqlObj> itemList = Arrays.asList(item, item1, item2);
        Collections.sort(itemList, new SqlObj.SqlObjComparator());
        String inertSql = SqlObjs.toIdempotenceInsertSql(itemList);
        String deleteSql = SqlObjs.toDeleteSql(itemList);
        String updateSql = SqlObjs.toUpdateSql(itemList);
        System.out.println(inertSql+"\n\n"+deleteSql+"\n\n"+updateSql);
    }
}
