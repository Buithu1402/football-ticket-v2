package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.request.payload.StadiumPayload;
import com.app.footballticketservice.service.StadiumService;
import com.app.footballticketservice.utils.Base64Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stadium")
public class StadiumController {
    private final StadiumService stadiumService;

    @GetMapping("seat/type")
    public Object findSeatType() {
        return ResponseContainer.success(stadiumService.findSeatType());
    }

    @GetMapping
    public Object findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "key", defaultValue = "") String key
    ) {
        return ResponseContainer.success(stadiumService.findAll(page, size, key));
    }

    @GetMapping("{stadiumId}/seats")
    public Object findById(@PathVariable("stadiumId") long stadiumId) {
        return ResponseContainer.success(stadiumService.findSeats(stadiumId));
    }

    @DeleteMapping("seat/{seatId}")
    public Object deleteSeat(@PathVariable("seatId") long seatId) {
        stadiumService.deleteSeat(seatId);
        return ResponseContainer.success("Xóa ghế thành công");
    }


    @PostMapping
    public Object save(
            @RequestPart("data") StadiumPayload payload,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        String url = null;
        if (file != null && file.getContentType().startsWith("image")) {
            url = Base64Utils.encodeImage(file);
        }
        stadiumService.save(payload, url);
        return ResponseContainer.success("Thêm sân vận động thành công");
    }

    @DeleteMapping("{stadiumId}")
    public Object delete(@PathVariable("stadiumId") long stadiumId) {
        stadiumService.delete(stadiumId);
        return ResponseContainer.success("Xóa sân vận động thành công");
    }
}
