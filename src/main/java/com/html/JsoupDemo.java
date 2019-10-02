package com.html;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

@Slf4j
public class JsoupDemo {


    public void parseHtml(String url) throws IOException {

        Document parent = Jsoup.parse(
                new URL("https://www.gotokeep.com/trainingpoint/54826e417fb786000069ad82?keyword=%E8%83%B8%E9%83%A8"),
                2000);

        log.info("{}", parent);

    }

}
