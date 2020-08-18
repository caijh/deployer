package com.github.caijh.deployer.controller;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.validation.Valid;

import com.github.caijh.deployer.model.Chart;
import com.github.caijh.deployer.request.ChartsReqBody;
import com.github.caijh.deployer.service.ChartService;
import com.github.caijh.framework.util.YamlUtils;
import com.google.common.io.Files;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 应用模板控制器.
 */
@RestController
public class ChartController {

    @Inject
    private ChartService chartService;

    /**
     * 应用模板chart列表.
     *
     * @param reqBody ChartsReqBody
     * @return Page&lt;hart&gt; 应用模板列表分页
     */
    @PostMapping(value = "/charts")
    public Page<Chart> charts(@RequestBody @Valid ChartsReqBody reqBody) {
        return chartService.list(PageRequest.of(reqBody.getPageNo(), reqBody.getPageSize()));
    }

    /**
     * 获取应用创建时，需要填写的配置问题.
     *
     * @param chartName    图表
     * @param chartVersion 图表版本
     * @return json of questions.yml
     * @throws IOException if read questions.yml fail.
     */
    @GetMapping(value = "/chart/{chartName}/{chartVersion}")
    public String questions(@PathVariable String chartName, @PathVariable String chartVersion) throws IOException {
        File file = chartService.findQuestionFile(chartName, chartVersion);

        return YamlUtils.toJsonString(Files.asCharSource(file, UTF_8).read());
    }

}
