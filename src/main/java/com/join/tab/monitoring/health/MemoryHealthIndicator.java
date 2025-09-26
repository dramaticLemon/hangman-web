package com.join.tab.monitoring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Component("memoryUsage")
public class MemoryHealthIndicator implements HealthIndicator {

   private static final double WARNING_THRESHOLD = 0.8; // 80% of max memory
   private static final double CRITICAL_THRESHOLD = 0.9; // 90% of max memory

   @Override
   public Health health() {
      try {
         // get memory usage info form JVM
         MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
         MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();

         long used = heapMemory.getUsed(); // memory currently used
         long max = heapMemory.getMax(); // maximum memory allowed
         double usageRatio = (double) used / max; // Calculate usage ratio

         Health.Builder healthBuilder;
         String status;

         // Decide health status based on memory usage
         if (usageRatio >= CRITICAL_THRESHOLD) {
            healthBuilder = Health.down(); // Mark health as DOWN if memory is critical
            status = "CRITICAL - Memory usage very high";
         } else if (usageRatio >= WARNING_THRESHOLD) {
            healthBuilder = Health.up(); // Memory high but not critical
            status = "WARNING - Memory usage high";
         } else {
            healthBuilder = Health.up(); // Memory usage normal
            status = "OK - Memory usage normal";
         }

         // Build Health object with details
         return healthBuilder
                 .withDetail("status", status)
                 .withDetail("usedMemory", formatBytes(used))
                 .withDetail("maxMemory", formatBytes(max))
                 .withDetail("usagePercentage", String.format("%.2f%%", usageRatio * 100))
                 .withDetail("freeMemory", formatBytes(max - used))
                 .build();

      } catch (Exception e) {
         // return DOWN if something goes wrong while checking memory
         return Health.down()
                 .withDetail("error", e.getMessage())
                 .withDetail("reason", "Failed to check memory usage")
                 .build();
      }
   }

   // Convert bytes to MB for easier reading
   private String formatBytes(long bytes) {
      double mb = bytes / (1024.0 * 1024.0);
      return String.format("%.2f MB", mb);
   }

}
