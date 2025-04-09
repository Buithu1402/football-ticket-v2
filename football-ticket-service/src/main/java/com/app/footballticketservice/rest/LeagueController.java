package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.League;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.service.LeagueService;
import com.app.footballticketservice.utils.Base64Utils;
import com.app.footballticketservice.utils.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("league")
@RequiredArgsConstructor
public class LeagueController {
    private final LeagueService leagueService;

    @GetMapping
    public Object findAll(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        var offset = PagingUtil.calculateOffset(page, size);
        var data = leagueService.findAll(offset, size, keyword);
        var numberOfRecords = leagueService.count(keyword);
        return ResponseContainer.success(new PagingContainer<>(page, size, numberOfRecords, data));
    }

    @PostMapping
    public Object create(
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "leagueId", defaultValue = "0") Long leagueId
    ) throws IOException {
        String url = null;
        if (logo != null && logo.getContentType().startsWith("image")) {
            url = Base64Utils.encodeImage(logo);
        }
        leagueService.save(new League(leagueId, name, url));
        return ResponseContainer.success("Insert league success");
    }

    @DeleteMapping("{leagueId}")
    public Object delete(@PathVariable(value = "leagueId") Long leagueId) {
        leagueService.delete(leagueId);
        return ResponseContainer.success("Delete league success");
    }
}
