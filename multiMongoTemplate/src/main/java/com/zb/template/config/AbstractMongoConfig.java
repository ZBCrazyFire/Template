package com.zb.template.config;

import com.mongodb.MongoClientURI;
import lombok.Data;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Data
public abstract class AbstractMongoConfig {
    protected String uri;

    protected MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClientURI(uri));
    }

    abstract public MongoTemplate getMongoTemplate() throws Exception;
}
