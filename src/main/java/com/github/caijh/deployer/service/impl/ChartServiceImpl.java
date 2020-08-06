package com.github.caijh.deployer.service.impl;

import java.io.File;

import com.github.caijh.deployer.config.props.ChartsProperties;
import com.github.caijh.deployer.exception.ChartNotFoundException;
import com.github.caijh.deployer.exception.ChartVersionNotFoundException;
import com.github.caijh.deployer.service.ChartService;
import org.springframework.stereotype.Service;

@Service
public class ChartServiceImpl implements ChartService {

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

}
