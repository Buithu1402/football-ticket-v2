package com.app.footballticketservice.model;

import java.util.List;

public record ChartData(
        List<String> labels,
        List<Datasets> datasets
) {

}
