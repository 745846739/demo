package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item extends SqlObj{
    private Long id;

    @SqlValue
    private String name;

    @SqlValue
    @SqlWhere
    private String key;

    @SqlValue
    @SqlSet
    private String value;

    @SqlValue
    @SqlWhere
    private Long namespaceId;

    @SqlValue
    @SqlOrder
    private Date approvalTime;

    private String itemOperationType;
}
