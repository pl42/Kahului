package com.pl42;

import com.pl42.kahului.mind.Kahului;
import com.pl42.kahului.utils.CalcUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KahuluiApplication {
  private static final Logger logger = Logger.getLogger(KahuluiApplication.class);

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(KahuluiApplication.class, args);
    Kahului dolores = context.getBean(Kahului.class);

    if (args.length < 2) {
      logger.error("Too few arguments given!");
      System.exit(-1);
    }
    if (args.length == 6) {
      logger.info("6 arguments provided. Proceeding to set Binance and Twitter credentials");
      dolores.setBinanceCreds(args[0], args[1]);
      dolores.setTwitterCreds(args[2], args[3], args[4], args[5]);
    } else if (args.length == 2) {
      logger.info("2 arguments provided. Proceeding to set Binance credentials");
      dolores.setBinanceCreds(args[0], args[1]);
    } else {
      logger.error("Incorrect number of arguments given!");
      System.exit(-1);
    }
    logger.info("Starting KAHULUI...");
    runKahului(dolores);
  }

  private static void runKahului(Kahului dolores) {
    for (; ; ) {
      try {
        dolores.gatherMindData();
        dolores.predictAndTrade();
      } catch (Exception e) {
        logger.error("There was an error during the main trading loop! {}", e);
      } finally {
        //dolores.printBalances();
        dolores.reset();
        new CalcUtils().sleeper(25000);
      }
    }
  }
}
