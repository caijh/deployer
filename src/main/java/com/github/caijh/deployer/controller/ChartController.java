package com.github.caijh.deployer.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import com.github.caijh.deployer.model.Chart;
import com.github.caijh.deployer.request.ChartsReqBody;
import com.github.caijh.deployer.service.ChartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
