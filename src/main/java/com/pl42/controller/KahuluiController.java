package com.pl42.controller;

import com.google.common.collect.EvictingQueue;
import com.google.common.hash.Hashing;
import com.pl42.kahului.mind.Kahului;
import com.pl42.kahului.utils.CalcUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

@RestController
public class KahuluiController {
  private static final Logger logger = Logger.getLogger(KahuluiController.class);
  private static final String PATH_BALANCE = "/balance/btc";
  private static final String PATH_PROFIT = "/balance/profit";
  private static final String PATH_SHUTDOWN = "/seppuku";
  private static final String PATH_STATUS = "/status";
  private static final String PATH_ORDER_HISTORY = "/orders";
  private static final String RESPONSE_SUFFIX = " endpoint hit";
  private final Kahului kahului;
  private Queue<Double> queue = EvictingQueue.create(100);

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
    double startTime = (double) System.nanoTime();
    Double currentPrice = kahului.getCurrentPrice();
    Double initialInvestment = kahului.getInitialInvestment();
    Double currentBalance = Double.valueOf(kahului.getCurrentBalance());
    Double portfolioValue = currentBalance * currentPrice;
    double balanceDiff = CalcUtils.roundTo(currentBalance - initialInvestment, 8);
    double balanceDiffUSD = CalcUtils.roundTo(balanceDiff * currentPrice, 2);
    StringBuilder response =
        new StringBuilder(
            "M\"\"MMM\"\"MMM\"\"M&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dP&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dP<br>M&nbsp;&nbsp;MMM&nbsp;&nbsp;MMM&nbsp;&nbsp;M&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;88&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;88<br>M&nbsp;&nbsp;MMP&nbsp;&nbsp;MMP&nbsp;&nbsp;M&nbsp;dP&nbsp;&nbsp;&nbsp;&nbsp;dP&nbsp;.d8888b.&nbsp;d8888P&nbsp;d8888P<br>M&nbsp;&nbsp;MM'&nbsp;&nbsp;MM'&nbsp;.M&nbsp;88&nbsp;&nbsp;&nbsp;&nbsp;88&nbsp;88'&nbsp;&nbsp;`88&nbsp;&nbsp;&nbsp;88&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;88<br>M&nbsp;&nbsp;`'&nbsp;.&nbsp;''&nbsp;.MM&nbsp;88.&nbsp;&nbsp;.88&nbsp;88.&nbsp;&nbsp;.88&nbsp;&nbsp;&nbsp;88&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;88<br>M&nbsp;&nbsp;&nbsp;&nbsp;.d&nbsp;&nbsp;.dMMM&nbsp;`8888P88&nbsp;`88888P8&nbsp;&nbsp;&nbsp;dP&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dP<br>MMMMMMMMMMMMMM&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.88<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;d8888P<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    response.append("<m>Version&nbsp;").append(kahului.getVersion()).append("</m><br>");
    if (Kahului.DEVELOPMENT_MODE) {
      response.append("<br>### DEVELOPMENT MODE ###");
    }
    response.append("<br>--- Status report ---");
    response.append("<br>Status: ").append(kahului.getCurrentStateString());
    response.append("<br>Investment: ").append(initialInvestment).append(" BTC");
    response
        .append("<br>Portfolio  ≈ ")
        .append(currentBalance)
        .append(" BTC ($")
        .append(String.format("%.2f", portfolioValue))
        .append(")");
    response.append(kahului.getBalances());
    response
        .append("<br>Profit: ")
        .append(kahului.getCurrentProfit())
        .append("% (")
        .append(String.format("%.8f", balanceDiff))
        .append(" BTC ≈ $")
        .append(String.format("%.2f", balanceDiffUSD))
        .append(")");
    if (!kahului.isEXECUTE_TWEETS()) {
      response.append("<br>Tweeting: DISABLED");
    }
    response.append("<br><br>--- Market ---");
    response.append("<br>BTC Price: $").append(String.format("%.2f", currentPrice));
    response.append("<br>Target: $").append(String.format("%.2f", kahului.getCurrentTargetPrice()));
    response
        .append("<br>Buy back: $")
        .append(String.format("%.2f", kahului.getCurrentBuyBackPrice()));
    response.append("<br>Sell confidence: ").append(kahului.getCurrentSellConfidence()).append("%");
    if (!kahului.currentState) {
      Double diff = kahului.getCurrentPrice() - kahului.getOpenBuyBackPrice();
      response.append("<br><br>--- Open buy back ---");
      response
          .append("<br>Amount: ")
          .append(kahului.getOpenBuyBackAmt())
          .append(" BTC @ $")
          .append(String.format("%.2f", kahului.getOpenBuyBackPrice()));
      response
          .append("<br>Difference: $")
          .append(String.format("%.2f", diff))
          .append(" (")
          .append(kahului.getOpenBuyBackPercentage())
          .append("%)");
    }
    response.append("<br><br>--- Links ---");
    response.append(
        "<br><a href=\"https://github.com/pl42/kahului\" style=\"color:#F7931A\">Source Code</a>");
    response.append(
        "<br><a href=\"https://twitter.com/WestworldKahului\" style=\"color:#F7931A\">Twitter</a>");
    response.append(
        "<br><a href=\"http://www.peggy42.cn:17071/orders\" style=\"color:#F7931A\">Order History</a>");
    response.append("<br><br>--- Donate ---");
    response.append(
        "<br>Personal: <a href=\"https://www.blockchain.com/btc/address/"
            + "14Xqn75eLQVZEgjFgrQzF8C2PxNDf894yj\" style=\"color:#F7931A\">14X...4yj</a>");
    response.append(
        "<br>Kahului: <a href=\"https://www.blockchain.com/btc/address/"
            + "1BWu4LtW1swREcDWffFHZSuK3VTT1iWuba\" style=\"color:#F7931A\">1BW...uba</a>");
    queue.add((System.nanoTime() - startTime) / 1000000000);
    response
        .append("<g><br><br>Avg load time: ")
        .append(String.format("%.4f", getAverageStatusLoadTime()))
        .append("s");
    response.append("<br>Uptime: ").append(CalcUtils.getUpTimeString()).append("</g>");
    return new ResponseEntity<>(
        new StringBuilder(
                "<html><head><link rel=\"apple-touch-icon\" sizes=\"180x180\" href=\"https://www.peggy42.cn/apple-touch-icon.png\"><link rel=\"icon\" type=\"image/png\" sizes=\"32x32\" href=\"https://www.peggy42.cn/favicon-32x32.png\"><link rel=\"icon\" type=\"image/png\" sizes=\"16x16\" href=\"https://www.peggy42.cn/favicon-16x16.png\"><link rel=\"manifest\" href=\"https://www.peggy42.cn/site.webmanifest\"><link rel=\"mask-icon\" href=\"https://www.peggy42.cn/safari-pinned-tab.svg\" color=\"#5bbad5\"><meta name=\"msapplication-TileColor\" content=\"#da532c\"><meta name=\"theme-color\" content=\"#ffffff\"><meta http-equiv=\"refresh\" content=\"25\" /><style>body {  color: #F7931A;}m {  color: #A9A9A9;}g {  color: #999999;}</style></head><title>Kahului</title><body bgcolor=\"#000000\"><font face=\"Courier\" size=\"3\">")
            .append(response)
            .append("</font></body></html>"),
        HttpStatus.OK);
  }

  @GetMapping(path = PATH_ORDER_HISTORY)
  public ResponseEntity getOrderHistory() {
    logger.trace(PATH_ORDER_HISTORY + RESPONSE_SUFFIX);
    String response = kahului.getOrderHistory();
    return new ResponseEntity<>(
        "<html>"
            + "<head>"
            + "<link rel=\"apple-touch-icon\" sizes=\"180x180\" href=\"https://www.peggy42.cn/apple-touch-icon.png\">"
            + "<link rel=\"icon\" type=\"image/png\" sizes=\"32x32\" href=\"https://www.peggy42.cn/favicon-32x32.png\">"
            + "<link rel=\"icon\" type=\"image/png\" sizes=\"16x16\" href=\"https://www.peggy42.cn/favicon-16x16.png\">"
            + "<link rel=\"manifest\" href=\"https://www.peggy42.cn/site.webmanifest\">"
            + "<link rel=\"mask-icon\" href=\"https://www.peggy42.cn/safari-pinned-tab.svg\" color=\"#5bbad5\">"
            + "<meta name=\"msapplication-TileColor\" content=\"#da532c\">"
            + "<meta name=\"theme-color\" content=\"#ffffff\">"
            + "<meta http-equiv=\"refresh\" content=\"25\" />"
            + "</head>"
            + "<title>Kahului</title>"
            + "<body bgcolor=\"#000000\">"
            + "<font face=\"Courier\" size=\"3\" color=\"#F7931A\">"
            + "<a href=\"http://www.peggy42.cn:17071/status\" style=\"color:#F7931A\">Back</a>"
            + response
            + "</font>"
            + "</body>"
            + "</html>",
        HttpStatus.OK);
  }

  /**
   * Returns the average of the queue
   *
   * @return Double average
   */
  private Double getAverageStatusLoadTime() {
    if (queue.size() == 0) {
      return null;
    }
    double average = 0.0;
    for (Double num : queue) {
      average += num / queue.size();
    }
    return average;
  }
}
