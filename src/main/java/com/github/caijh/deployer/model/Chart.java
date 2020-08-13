package com.github.caijh.deployer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.caijh.framework.orm.model.BaseEntity;
import lombok.Data;

/**
 * 应用模板信息.
 */
@Entity
@Data
public class Chart implements BaseEntity<Long> {

    /**
     * 应用模板id.
     */
    @Id
    private Long id;

    /**
     * 应用模板名称.
     */
    private String name;

    /**
     * 应用模板版本.
     */
    private String version;

    /**
     * 应用版本.
     */
    private String appVersion;

}
