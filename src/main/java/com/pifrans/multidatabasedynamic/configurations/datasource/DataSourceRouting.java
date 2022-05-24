package com.pifrans.multidatabasedynamic.configurations.datasource;

import com.pifrans.multidatabasedynamic.repositories.databases.DatabaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Lazy
@Component
@RequiredArgsConstructor
public class DataSourceRouting extends AbstractRoutingDataSource {
    private final DatabaseRepository databaseRepository;


    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContext.get();
    }

    @Autowired
    @Qualifier("othersDataSource")
    public void setDataSources() {
        this.setTargetDataSources(dataSources());
        this.setDefaultTargetDataSource(defaultDataSource());
        this.afterPropertiesSet();
    }

    private DataSource defaultDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/data_01");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    private Map<Object, Object> dataSources() {
        var databases = databaseRepository.findAll();
        var dataSources = new HashMap<>();

        for (var database : databases) {
            var dataSource = new DriverManagerDataSource();
            dataSource.setUrl(database.getUrl());
            dataSource.setUsername("root");
            dataSource.setPassword("topsecret");
            dataSources.put(database.getName(), dataSource);
        }
        return dataSources;
    }
}
