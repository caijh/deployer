package com.github.caijh.deployer.service.impl;

import java.io.File;
import javax.inject.Inject;

import com.github.caijh.deployer.config.props.ChartsProperties;
import com.github.caijh.deployer.exception.ChartNotFoundException;
import com.github.caijh.deployer.exception.ChartVersionNotFoundException;
import com.github.caijh.deployer.model.Chart;
import com.github.caijh.deployer.repository.ChartRepository;
import com.github.caijh.deployer.service.ChartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChartServiceImpl implements ChartService {

    @Inject
    private ChartRepository chartRepository;


    @Override
    public String findChartPath(String chartName, String chartVersion) {
        File chartsDir = ChartsProperties.chartsDir;

        File[] chart = chartsDir.listFiles((dir, name) -> chartName.equalsIgnoreCase(name));
        if (chart == null || chart.length <= 0) {
            throw new ChartNotFoundException();
        }

        File[] matchChart = chart[0].listFiles((dir, name) -> name.equalsIgnoreCase(chartVersion));
        if (matchChart == null || matchChart.length <= 0) {
            throw new ChartVersionNotFoundException();
        }

        return matchChart[0].getPath();
    }

    @Override
    public Page<Chart> list(Pageable pageable) {
        return chartRepository.findAll(pageable);
    }

    @Override
    public File findQuestionFile(String chartName, String chartVersion) {
        String chartPath = findChartPath(chartName, chartVersion);

        return new File(chartPath + File.separator + "questions.yml");
    }

}
