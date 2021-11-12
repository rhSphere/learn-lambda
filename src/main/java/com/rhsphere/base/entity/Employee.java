package com.rhsphere.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-08-25 11:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String name;
    private Integer age;
}
