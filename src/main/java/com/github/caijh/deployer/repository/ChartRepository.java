package com.github.caijh.deployer.repository;

import com.github.caijh.deployer.model.Chart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartRepository extends JpaRepository<Chart, Long> {

}
