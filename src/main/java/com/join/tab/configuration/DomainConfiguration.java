package com.join.tab.configuration;

import com.join.tab.domain.repository.WordRepository;
import com.join.tab.domain.service.GameFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public GameFactory gameFactory(WordRepository wordRepository) {
        return new GameFactory(wordRepository);
    }
}