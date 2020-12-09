package com.pl42.kahului;

import com.pl42.kahului.mind.Kahului;
import com.pl42.kahului.utils.CalcUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KahuluiApplication {
  private final static Logger logger = Logger.getLogger(KahuluiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(KahuluiApplication.class, args);
    logger.info("Starting KAHULUI (v6.0.0) ...");
    if (args.length < 6) {
      logger.error("Not enough arguments have been given");
      System.exit(-1);
    }
    logger.info("Starting Kahului trading...");
    for (;;) {
      Kahului dolores = new Kahului(args[0], args[1]);
      dolores.setTwitterCreds(args[2], args[3], args[4], args[5]);
      dolores.gatherMindData();
      dolores.predictAndTrade();
      dolores.printBalances();
      new CalcUtils().sleeper(25000);
    }
  }
}
