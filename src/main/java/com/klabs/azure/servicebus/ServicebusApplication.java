package com.klabs.azure.servicebus;

import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.azure.spring.messaging.checkpoint.Checkpointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static com.azure.spring.messaging.AzureHeaders.CHECKPOINTER;
import static  com.azure.spring.messaging.AzureHeaders.MESSAGE_SESSION;

@SpringBootApplication
public class ServicebusApplication implements CommandLineRunner {


	private static final Logger LOGGER = LoggerFactory.getLogger(ServicebusApplication.class);
	private static final Sinks.Many<Message<String>> many = Sinks.many().unicast().onBackpressureBuffer();



//	@Bean
//	public Supplier<Flux<Message<String>>> supply() {
//		return ()->many.asFlux()
//				.doOnNext(m->LOGGER.info("Manually sending message {}", m))
//				.doOnError(t->LOGGER.error("Error encountered", t));
//	}

	@Bean
	@Profile(value = {"kedapocsub1","kedapocsub2","kedapocsub3"})
	public Consumer<Message<String>> consume() {
		return message->{
			Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);
			LOGGER.info("New message received: '{}'", message.getPayload());
			checkpointer.success()
					.doOnSuccess(s->LOGGER.info("Message '{}' successfully checkpointed", message.getPayload()))
					.doOnError(e->LOGGER.error("Error found", e))
					.block();
		};
	}
	@Bean
	@Profile("kedapocsub1")
	public Consumer<Message<String>> dlqInputSub1() {
		return message -> {
			System.out.println("Received message from DLQ: " + message.getPayload());
			// Optional: Inspect dead-letter reason/description headers here
		};
	}

	@Bean
	@Profile("kedapocsub2")
	public Consumer<Message<String>> dlqInputSub2() {
		return message -> {
			System.out.println("Received message from DLQ: " + message.getPayload());
			// Optional: Inspect dead-letter reason/description headers here


		};
	}

	@Bean
	@Profile("kedapocsub3")
	public Consumer<Message<String>> dlqInputSub3() {
		return message -> {
			System.out.println("Received message from DLQ: " + message.getPayload());
			// Optional: Inspect dead-letter reason/description headers here


		};
	}



	public static void main(String[] args) {
		SpringApplication.run(ServicebusApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Going to add message {} to Sinks.Many.", "Hello World");
		many.emitNext(MessageBuilder.withPayload("Hello World").build(), Sinks.EmitFailureHandler.FAIL_FAST);
	}
}

