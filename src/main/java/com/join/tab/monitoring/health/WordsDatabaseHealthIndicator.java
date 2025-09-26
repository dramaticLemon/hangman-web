package com.join.tab.monitoring.health;

import com.join.tab.infra.repository.jpa.WordJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("worksDatabase")
public class WordsDatabaseHealthIndicator implements HealthIndicator {
    private static final Logger log = LoggerFactory.getLogger(WordsDatabaseHealthIndicator.class);
    private final WordJpaRepository wordJpaRepository;

    public WordsDatabaseHealthIndicator (WordJpaRepository wordJpaRepository) {
        this.wordJpaRepository = wordJpaRepository;
    }

    @Override
    public Health health() {
       try {
           // count total word in database
           long totalWords = wordJpaRepository.count();
           // count only active word
           long activeWords = wordJpaRepository.countByIsActiveTrue();

           // if no active words, mark health as DOWN
           if (activeWords == 0) {
               return Health.down()
                       .withDetail("reason", "No active words available")
                       .withDetail("totalWords", totalWords)
                       .withDetail("activeWords", activeWords)
                       .build();
           }

           // Otherwise, health is UP and include details
           return Health.up()
                   .withDetail("totalWords", totalWords)
                   .withDetail("activeWords", activeWords)
                   .withDetail("supportedLanguages", wordJpaRepository.findSupportedLanguages())
                   .withDetail("status", "Words database is healthy")
                   .build();

       } catch (Exception e) {
           // log error and mark health ad DOWN if something goes wrong
          log.error("Health check failed for words database",e);
          return Health.down()
                  .withDetail("error", e.getMessage())
                  .withDetail("reason", "Database connection failed")
                  .build();
       }
    }
}
