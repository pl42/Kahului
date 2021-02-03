package com.pl42.controller;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.pl42.kahului.mind.Kahului;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
public class KahuluiController {
  private static final Logger logger = Logger.getLogger(KahuluiController.class);
  private static final String PATH_BALANCE = "/balance/btc";
  private static final String PATH_PROFIT = "/balance/profit";
  private static final String PATH_SHUTDOWN = "/seppuku";
  private static final String PATH_STATUS = "/status";
  private static final String PATH_OPEN_ORDERS = "/orders";
  private static final String RESPONSE_SUFFIX = " endpoint hit";
  private final Kahului kahului;

  @Autowired
  public KahuluiController(Kahului kahului) {
    this.kahului = kahului;
  }

  @GetMapping(path = PATH_BALANCE)
  public ResponseEntity getTotalBTC() {
    logger.trace(PATH_BALANCE + RESPONSE_SUFFIX);
    return new ResponseEntity<>(kahului.getCurrentBalance(), HttpStatus.OK);
  }

  @GetMapping(path = PATH_PROFIT)
  public ResponseEntity getTotalProfit() {
    logger.trace(PATH_PROFIT + RESPONSE_SUFFIX);
    return new ResponseEntity<>(kahului.getCurrentProfit(), HttpStatus.OK);
  }

  @GetMapping(
      path = PATH_SHUTDOWN,
      params = {"pass"})
  public void seppuku(@RequestParam("pass") String pass, HttpServletRequest request) {
    logger.trace(PATH_SHUTDOWN + RESPONSE_SUFFIX);
    // Verify the password provided...
    String sha256hex = Hashing.sha256().hashString(pass, StandardCharsets.UTF_8).toString();
    if (sha256hex.equals("bc159b2d00a17af10d15f85c0fc3050626a9de62ddada278c086b5a53c883464")) {
      logger.info("Shutdown received from IP-address: " + request.getRemoteUser());
      System.exit(-1);
    } else {
      logger.info("Incorrect shutdown code from IP-address: " + request.getRemoteAddr());
    }
  }

  @GetMapping(path = PATH_STATUS)
  public ResponseEntity getState() {
    logger.trace(PATH_STATUS + RESPONSE_SUFFIX);
    String response = "=====  >>>>>  KAHULUI (v" + kahului.getVersion() + ") <<<<<  =====<br>";
    if (Kahului.DEVELOPMENT_MODE) response += "<br>### DEVELOPMENT MODE ###<br>";
    response += "<br>Status  :::  " + kahului.getCurrentStateString();
    response += "<br><br>--- Engine data ---";
    response += "<br>BTC Price: $" + kahului.getCurrentPrice();
    response += "<br>Target: $" + kahului.getCurrentTargetPrice();
    response += "<br>Buy back: $" + kahului.getCurrentBuyBackPrice();
    response += "<br><br>--- Status report ---";
    response += "<br>Sell confidence: " + kahului.getCurrentSellConfidence() + "%";
    if (!kahului.currentState)
      response +=
          "<br>There is an open buy back order at: $"
              + kahului.getOpenBuyBackPrice()
              + " for "
              + kahului.getOpenBuyBackAmt()
              + " BTC";
    response += "<br>Initial investment: " + kahului.getInitialInvestment() + " BTC";
    response += "<br>Portfolio value: " + kahului.getCurrentBalance() + " BTC";
    response += "<br>Profit: " + kahului.getCurrentProfit() + "%";
    response += "<br><br>--- Donate ---<br>14Xqn75eLQVZEgjFgrQzF8C2PxNDf894yj";
    return new ResponseEntity<>(
        "<html>\n"
            + "<head>\n"
            + "<link rel=\"apple-touch-icon\" sizes=\"180x180\" href=\"/apple-touch-icon.png\">\n"
            + "<link rel=\"icon\" type=\"image/png\" sizes=\"32x32\" href=\"/favicon-32x32.png\">\n"
            + "<link rel=\"icon\" type=\"image/png\" sizes=\"16x16\" href=\"/favicon-16x16.png\">\n"
            + "<link rel=\"manifest\" href=\"/site.webmanifest\">\n"
            + "<link rel=\"mask-icon\" href=\"/safari-pinned-tab.svg\" color=\"#5bbad5\">\n"
            + "<meta name=\"msapplication-TileColor\" content=\"#da532c\">\n"
            + "<meta name=\"theme-color\" content=\"#ffffff\">\n"
            + "</head>\n"
            + "<title>Kahului</title>\n"
            + "<body bgcolor=\"#000000\">\n"
            + "<font face=\"Courier\" size=\"3\" color=\"#F7931A\">\n"
            + response
            + "</font> \n"
            + "</body>\n"
            + "</html> ",
        HttpStatus.OK);
  }

  @GetMapping(path = PATH_OPEN_ORDERS)
  public ResponseEntity getOpenOrders() {
    logger.trace(PATH_OPEN_ORDERS + RESPONSE_SUFFIX);
    return new ResponseEntity<>(new Gson().toJson(kahului.getOpenOrders()), HttpStatus.OK);
  }
}
