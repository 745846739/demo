package com.example.demo;

public class Demo {
    public static void main(String[] args) {
        Item item = Item.builder().id(1L).name("test").key("testKey").value("testValue").namespaceId(2L).build();
        String inertSql = item.toIdempotenceInsertSql();
        String deleteSql = item.toDeleteSql();
        String updateSql = item.toUpdateSql();
        System.out.println(inertSql+"\n\n"+deleteSql+"\n\n"+updateSql);
    }
}
