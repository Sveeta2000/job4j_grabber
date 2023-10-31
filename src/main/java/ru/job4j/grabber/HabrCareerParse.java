package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static int idCount = 0;
    private final DateTimeParser dateTimeParser;
    private static final String SOURCE_LINK = "https://career.habr.com";

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> listOfPosts = new ArrayList<>();
        Connection connection;
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
        for (int i = 1; i <= 5; i++) {
            connection = Jsoup.connect(link + i);
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> listOfPosts.add(hcp.createPost(row)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return listOfPosts;
    }

    private Post createPost(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        String vacancyName = titleElement.text();
        Element linkElement = titleElement.child(0);
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        Element dateElement = element.select(".vacancy-card__date").first();
        Element date = dateElement.child(0);
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
        LocalDateTime localDateTime = hcp.dateTimeParser.parse(date.attr("datetime"));
        String description;
        try {
            description = hcp.retrieveDescription(link);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Post(idCount++, vacancyName, link, description, localDateTime);
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element textElement = document.select(".vacancy-description__text").first();
        return textElement.text();
    }
}
