package com.ngleanhvu.dsa_training_system.util;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class AppUtil {
    public static long megabytesToBytes(int megabytes) {
        return ((long) megabytes * 1024 * 1024);
    }

    public static SubmissionStatus fromValue(String value) {
        for (SubmissionStatus status : SubmissionStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy SubmissionStatus với value: " + value);
    }

    public static Object convertToJsonCompatible(Object value) {
        if (value instanceof LocalDate date) {
            return date.toString();
        }
        return value;
    }

    public static String sanitize(String inputHtml) {
        Safelist safelist = Safelist.relaxed()
                .addTags("table", "thead", "tbody", "tfoot", "tr", "td", "th")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("a", "href", "title")
                .addProtocols("img", "src", "http", "https", "data")
                .addProtocols("a", "href", "http", "https", "mailto");
        return Jsoup.clean(inputHtml, safelist);
    }

    public static LocalDateTime changeFormatDate(Object createdAtObj) {
        LocalDateTime createdAt = null;
        if (createdAtObj instanceof java.sql.Timestamp) {
            createdAt = ((java.sql.Timestamp) createdAtObj).toLocalDateTime();
        } else if (createdAtObj instanceof java.time.LocalDateTime) {
            createdAt = (LocalDateTime) createdAtObj;
        } else if (createdAtObj instanceof String) {
            createdAt = LocalDateTime.parse((String) createdAtObj);
        }
        return createdAt;
    }

    public static String changeSortBy(String sortOrder) {
        if (sortOrder.equals("createdAt")) {
            return "created_at";
        }
        if (sortOrder.equals("upVotes")) {
            return "up_votes";
        }
        if (sortOrder.equals("content")) {
            return "content";
        }
        return "created_at";
    }
}
