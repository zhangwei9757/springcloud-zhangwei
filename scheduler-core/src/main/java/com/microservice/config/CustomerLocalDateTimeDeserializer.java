package com.microservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author zhangwei
 * @date 2020-6-19 20:42:1
 **/
public class CustomerLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return this.transformLocalDateTime(jsonParser);
    }

    private LocalDateTime transformLocalDateTime(JsonParser jsonParser) throws IOException, JsonProcessingException {
        String oldDate = jsonParser.getText();
        try {
            oldDate = oldDate.replace("Z", " UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            Date date = df.parse(oldDate);
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            } catch (Exception e2) {
                try {
                    return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e3) {
                    try {
                        LocalDate localDate = LocalDate.parse(oldDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        return LocalDateTime.of(localDate, LocalTime.of(0, 0, 0));
                    } catch (Exception e4) {
                        return LocalDateTime.ofEpochSecond(jsonParser.getLongValue() / 1000, 0, ZoneOffset.ofHours(8));
                    }
                }
            }
        }

    }

    @Override
    public Class<?> handledType() {
        return LocalDateTime.class;
    }
}