package com.github.caijh.deployer.config.mysql;

import java.sql.Types;

import org.hibernate.dialect.MySQL8Dialect;

public class CustomMySQLDialect extends MySQL8Dialect {

    public CustomMySQLDialect() {
        super();
        this.registerColumnType(Types.JAVA_OBJECT, "json");
        this.registerColumnType(Types.OTHER, "json");
    }

}
