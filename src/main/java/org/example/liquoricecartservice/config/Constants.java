package org.example.liquoricecartservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class Constants {
    public final static String BASE_PATH = "/api/v1";
}
