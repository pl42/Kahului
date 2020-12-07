package com.pl42.kahului;

import com.pl42.kahului.mind.Kahului;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KahuluiApplication {
  private final static Logger logger = Logger.getLogger(KahuluiApplication.class);

	public static void main(String[] args) {
    logger.info("Starting KAHULUI (v5.1.7) ...");
    if (args.length < 6) {
      logger.error("Not enough arguments have been given");
      System.exit(-1);
    }
		ConfigurableApplicationContext context = SpringApplication.run(KahuluiApplication.class, args);
    Kahului kahului = context.getBean(Kahului.class);
    kahului.startTrading();
	}
}
