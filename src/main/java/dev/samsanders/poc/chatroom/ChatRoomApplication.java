package dev.samsanders.poc.chatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import software.amazon.kinesis.coordinator.Scheduler;

@SpringBootApplication
public class ChatRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRoomApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent applicationReadyEvent) {
        final ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        final Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        final TaskExecutor taskExecutor = applicationContext.getBean(TaskExecutor.class);

        taskExecutor.execute(scheduler);
    }

    @Bean("taskExecutor")
    @Profile("test")
    public TaskExecutor testTaskExecutor() {
        return runnable -> {
            // do nothing
        };
    }
}
