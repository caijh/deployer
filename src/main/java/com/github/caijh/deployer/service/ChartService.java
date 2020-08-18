package com.github.caijh.deployer.service;

import java.io.File;

import com.github.caijh.deployer.model.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChartService {

    String findChartPath(String chartName, String chartVersion);

    Page<Chart> list(Pageable pageable);

    File findQuestionFile(String chartName, String chartVersion);

}
