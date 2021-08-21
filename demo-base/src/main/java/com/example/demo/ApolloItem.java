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
@SqlUnderline
public class ApolloItem extends SqlObj{
    @SqlOrder
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
    @SqlUnderline
    private Date approvalTime;

    @SqlType
    private String itemOperationType;
}
