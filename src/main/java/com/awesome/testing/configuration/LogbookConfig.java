package com.awesome.testing.configuration;

import static org.zalando.logbook.HeaderFilter.none;

import java.util.regex.Pattern;

import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpLogFormatter;

@Configuration
public class LogbookConfig {

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("(?m)^");
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Bean
    public BodyFilter bodyFilter(ObjectMapper objectMapper) {
        return (contentType, body) -> {
            if (contentType != null && contentType.contains("json")) {
                try {
                    Object json = objectMapper.readValue(body, Object.class);
                    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    prettyJson = NEWLINE_PATTERN.matcher(prettyJson).replaceAll("    ");
                    return "\n" + prettyJson;
                } catch (Exception e) {
                    // ignore parsing errors and return the original body
                }
            }
            return body;
        };
    }

    @Bean
    public HttpLogFormatter httpLogFormatter(ObjectMapper objectMapper) {
        return new JsonHttpLogFormatter(objectMapper);
    }

    @Bean
    public Logbook logbook(BodyFilter bodyFilter, HttpLogWriter writer, HttpLogFormatter formatter) {
        return Logbook.builder()
                .sink(new DefaultSink(formatter, writer))
                .bodyFilter(bodyFilter)
                .build();
    }

    @Bean
    public HeaderFilter headerFilter() {
        return none();
    }
}