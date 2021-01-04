package com.pl42.controller;

import com.google.gson.Gson;
import com.pl42.kahului.mind.Kahului;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KahuluiController {
  private static final Logger logger = Logger.getLogger(KahuluiController.class);
  private static final String PATH_BALANCE = "/balance/btc";
  private static final String PATH_PROFIT = "/balance/profit";
  private static final String PATH_SHUTDOWN = "/seppuku";
  private static final String PATH_STATUS = "/status";
  private static final String PATH_OPEN_ORDERS = "/orders";
  private final Kahului kahului;

  @Autowired
  public KahuluiController(Kahului kahului) {
    this.kahului = kahului;
  }

  @RequestMapping(path = PATH_BALANCE, method = RequestMethod.GET)
  public ResponseEntity getTotalBTC() {
    logger.trace(PATH_BALANCE + " endpoint hit");
    return new ResponseEntity<>(kahului.getTotalBalance(), HttpStatus.OK);
  }

  @RequestMapping(path = PATH_PROFIT, method = RequestMethod.GET)
  public ResponseEntity getTotalProfit() {
    logger.trace(PATH_PROFIT + " endpoint hit");
    return new ResponseEntity<>(kahului.getTotalProfit(), HttpStatus.OK);
  }

  @RequestMapping(path = PATH_SHUTDOWN, method = RequestMethod.GET)
  public void seppuku() {
    logger.trace(PATH_SHUTDOWN + " endpoint hit");
    logger.info("Shutdown down now...");
    System.exit(-1);
  }

  @RequestMapping(path = PATH_STATUS, method = RequestMethod.GET)
  public ResponseEntity getState() {
    logger.trace(PATH_STATUS + " endpoint hit");
    String response = "Have you ever seen anything so full of splendor?";
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(path = PATH_OPEN_ORDERS, method = RequestMethod.GET)
  public ResponseEntity getOpenOrders() {
    logger.trace(PATH_OPEN_ORDERS + " endpoint hit");
    return new ResponseEntity<>(new Gson().toJson(kahului.getOpenOrders()), HttpStatus.OK);
  }
}
