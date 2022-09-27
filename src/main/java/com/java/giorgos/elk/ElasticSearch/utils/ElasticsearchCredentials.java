package com.java.giorgos.elk.ElasticSearch.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchCredentials {
    @Value("${credentials.username}")
    public String username;
    @Value("${credentials.password}")
    public String password;
}
