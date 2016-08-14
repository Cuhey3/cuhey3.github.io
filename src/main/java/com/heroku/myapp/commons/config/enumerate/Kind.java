package com.heroku.myapp.commons.config.enumerate;

import static com.heroku.myapp.commons.util.actions.DiffUtil.commonDiff;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.stream.Collectors;

public enum Kind {

    female_seiyu_category_members(commonDiff(), "period=5m&delay=1m"),
    male_seiyu_category_members(commonDiff(), "period=5m&delay=2m"),
    seiyu_category_members(commonDiff()),
    seiyu_template_include_pages(commonDiff(), "period=5m&delay=3m"),
    seiyu_category_members_include_template(commonDiff()),
    koepota_events(commonDiff("url"), "period=30m&delay=10m"),
    koepota_seiyu(commonDiff()),
    seiyu_has_recentchanges(commonDiff()),
    koepota_seiyu_all(commonDiff()),
    amiami_item("period=60m&delay=30m"),
    amiami_original_titles(commonDiff("amiami_title")),
    amiami_original_titles_all(commonDiff("amiami_title")),
    google_trends_seiyu_all(commonDiff()),
    google_trends("period=65m&delay=10m"),
    test;

    private Kind(String... token) {
        for (String t : token) {
            if (t.contains("common_diff_key=")) {
                this.useCommonDiff = true;
                this.commonDiffKey = t.replace("common_diff_key=", "");
            } else if (t.contains("period=")) {
                this.timerUri = String.format("timer:%s?%s", this.name(), t);
            } else if (t.equals("develop")) {
                this.useDevelop = true;
            }
        }
        String resourcePath = "/message/" + this.name() + ".json";
        InputStream resourceAsStream = com.heroku.myapp.App.class
                .getResourceAsStream(resourcePath);
        try (BufferedReader buffer
                = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"))) {
            preMessage = buffer.lines().collect(Collectors.joining("\n"));
            System.out.println("loaded... " + resourcePath);
        } catch (Exception ex) {
            System.out.println("premessage initialization failed..."
                    + "\nSystem is shutting down.");
            System.out.println(resourcePath);
            System.exit(1);
        }
    }

    private String timerUri, preMessage;
    private boolean useCommonDiff = false;
    private String commonDiffKey;
    private boolean useDevelop = false;

    public String expression() {
        if (useDevelop) {
            return this.name() + "_develop";
        } else {
            return this.name();
        }
    }

    public String timerUri() {
        return this.timerUri;
    }

    public String commonDiffKey() {
        return this.commonDiffKey;
    }

    public void timerParam(String timerParam) {
        this.timerUri = String.format("timer:%s?%s", expression(), timerParam);
    }

    public boolean isUsedCommonDiffRoute() {
        return this.useCommonDiff;
    }

    public static Kind findKindByClassName(Object object) {
        try {
            String kindCamel = object.getClass().getSimpleName()
                    .replace("Snapshot", "").replace("Diff", "")
                    .replace("Consumer", "");
            String kindSnake
                    = String.join("_", kindCamel.split("(?=[\\p{Upper}])"))
                    .toLowerCase(Locale.US);
            return Kind.valueOf(kindSnake);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public String preMessage() {
        return this.preMessage;
    }
}
