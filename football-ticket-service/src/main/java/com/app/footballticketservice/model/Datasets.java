package com.app.footballticketservice.model;

import java.math.BigDecimal;
import java.util.List;

public record Datasets(
        List<BigDecimal> data,
        String label
) {
}
