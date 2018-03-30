package com.hiekn.boot.autoconfigure.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@ConditionalOnClass({Client.class,PreBuiltTransportClient.class})
@ConditionalOnMissingBean(type = {"org.elasticsearch.client.transport.TransportClient"})
@EnableConfigurationProperties({ElasticsearchProperties.class})
public class ElasticsearchAutoConfiguration {

    private ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchAutoConfiguration(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    @Bean
    public TransportClient transportClient(){
        Settings settings = Settings.builder().put("cluster.name", elasticsearchProperties.getClusterName()).build();
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        try {
            for (String host : elasticsearchProperties.getHost()) {
                String[] ipPort = StringUtils.tokenizeToStringArray(host,":");
                transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]), Integer.valueOf(ipPort[1])));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return transportClient;
    }

}
