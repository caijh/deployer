package com.github.caijh.deployer.config.mysql;

import java.sql.Types;

import org.hibernate.dialect.MySQL8Dialect;

/**
 * MySQL dialect 加入 json 类型.
 */
public class CustomMySqlDialect extends MySQL8Dialect {

    public CustomMySqlDialect() {
        super();
        this.registerColumnType(Types.JAVA_OBJECT, "json");
        this.registerColumnType(Types.OTHER, "json");
    }

}
