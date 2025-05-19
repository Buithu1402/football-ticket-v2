package com.app.footballticketservice.rest;

import com.app.footballticketservice.dto.ScoreMatchDTO;
import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.request.payload.MatchListPayload;
import com.app.footballticketservice.request.payload.MatchTicketPayload;
import com.app.footballticketservice.request.payload.UpsertMatchPayload;
import com.app.footballticketservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.graalvm.polyglot.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("match")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;

    @GetMapping("{matchId}/seats")
    public Object seats(@PathVariable long matchId) {
        return ResponseContainer.success(matchService.getSeats(matchId));
    }

    @PostMapping("all")
    public Object list(@RequestBody MatchListPayload payload) {
        return ResponseContainer.success(matchService.findAll(payload));
    }

    @GetMapping("{matchId}/score")
    public Object score(@PathVariable long matchId) {
        return ResponseContainer.success(matchService.getScore(matchId));
    }

    @PostMapping("score")
    public Object score(@RequestBody ScoreMatchDTO payload) {
        matchService.saveScore(payload);
        return ResponseContainer.success("Cập nhật tỷ số thành công");
    }

    @GetMapping("{matchId}/tickets")
    public Object tickets(@PathVariable long matchId) {
        return ResponseContainer.success(matchService.getTickets(matchId));
    }

    @PostMapping("tickets")
    public Object tickets(@RequestBody List<MatchTicketPayload> payloads) {
        var result = matchService.upsertTicket(payloads);
        return CollectionUtils.isEmpty(result)
                ? ResponseContainer.success("Tạo vé thành công")
                : ResponseContainer.error(result);
    }

    @PostMapping("upsert")
    public Object upsert(@RequestBody UpsertMatchPayload payload) {
        matchService.save(payload);
        return ResponseContainer.success("Tạo trận đấu thành công");
    }

    @PostMapping("test")
    public Object test() {
        var widgetLink = "";
        try {
            var doc = Jsoup.connect("").userAgent("Mozilla/5.0").get();
            var html = doc.select("script")
                          .stream()
                          .filter(script -> script.data().startsWith("window"))
                          .findFirst()
                          .map(Element::data)
                          .orElse("");
            try (var context = Context.create()) {
                context.eval("js", "var window = {};");
                context.eval("js", html);
                var eval = context.eval("js", "window.__NUXT__.state.football.detail.WebMatchData.animation.url");
                widgetLink = eval.asString();
            }
        } catch (Exception e) {
        }
        return ResponseContainer.success("Success");
    }

}
