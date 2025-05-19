package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.model.Team;
import com.app.footballticketservice.service.TeamService;
import com.app.footballticketservice.utils.Base64Utils;
import com.app.footballticketservice.utils.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping
    public Object findAll(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        var offset = PagingUtil.calculateOffset(page, size);
        var data = teamService.findAll(offset, size, keyword);
        var total = teamService.count(keyword);
        return ResponseContainer.success(new PagingContainer<>(page, size, total, data));
    }

    @PostMapping("upsert")
    public Object upsert(
            @RequestPart("data") Team data,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) throws IOException {
        if (logo != null && logo.getContentType().startsWith("image")) {
            var url = Base64Utils.encodeImage(logo);
            data.setLogo(url);
        }
        teamService.save(data);
        return ResponseContainer.success("Thành công");
    }

    @DeleteMapping
    public Object delete(@RequestParam("teamId") Long teamId) {
        teamService.delete(teamId);
        return ResponseContainer.success("Thành công");
    }

}
