package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("statistic")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("ticket")
    public Object ticket(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseContainer.success(statisticService.getTicketStatistic(page, size));
    }

    @GetMapping("revenue")
    public Object getRevenue(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("type") String type
    ) {
        return ResponseContainer.success(statisticService.getRevenue(from, to, type));
    }

    @GetMapping("revenue-league")
    public Object getLeagueRevenue(
            @RequestParam("from") String from,
            @RequestParam("to") String to
    ) {
        return ResponseContainer.success(statisticService.revenueByLeague(from, to));
    }

    @GetMapping("league")
    public Object getLeagueStatistic() {
        return ResponseContainer.success(statisticService.getLeagueStatistic());
    }
}
