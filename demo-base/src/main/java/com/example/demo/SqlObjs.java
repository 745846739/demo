package com.example.demo;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SqlObjs {
    public static String toIdempotenceInsertSql(List<SqlObj> sqlObjList){
        return sqlObjList.stream().map(SqlObj::toIdempotenceInsertSql).collect(Collectors.joining("\n\n"));
    }

    public static String toDeleteSql(List<SqlObj> sqlObjList) {
        return sqlObjList.stream().map(SqlObj::toDeleteSql).collect(Collectors.joining("\n\n"));
    }

    public static String toUpdateSql(List<SqlObj> sqlObjList) {
        return sqlObjList.stream().map(SqlObj::toUpdateSql).collect(Collectors.joining("\n\n"));
    }
}
