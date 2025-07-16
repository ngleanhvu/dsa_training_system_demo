package com.ngleanhvu.dsa_training_system.util;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.time.LocalDate;

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
}
