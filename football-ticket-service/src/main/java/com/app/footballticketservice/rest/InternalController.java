package com.app.footballticketservice.rest;

import com.app.footballticketservice.model.Message;
import com.app.footballticketservice.model.ResponseContainer;
import com.app.footballticketservice.ws.NotifyEnpoint;
import jakarta.websocket.EncodeException;
import org.graalvm.polyglot.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("internal")
public class InternalController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("send")
    public Object send() throws EncodeException, IOException {
        var message = new Message("system", "everyone", "Hello World");
        NotifyEnpoint.broadcast(message);
        return "OK";
    }

    @GetMapping("widget")
    public Object widget() throws IOException {
        var url = "https://www.aiscore.com/match-real-madrid-fc-barcelona/vrqwnigd6pnt4qn";
        var doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
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
            return ResponseContainer.success(eval.asString());
        }
    }

    @GetMapping("proxy")
    public Object proxyContent() {
        String url = "https://widgets.thesports01.com/en/3d/football?profile=74rekh26eseunr0&id=4261707";
        var headers = new HttpHeaders();
        headers.set("Referer", "https://www.aiscore.com/");
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            // Fetch content from thesports01.com
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return ResponseContainer.success(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching content");
        }
    }
}
