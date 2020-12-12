package com.pl42.controller;

import com.pl42.kahului.mind.Kahului;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KahuluiController {
  @Autowired
  private Kahului dolores;

  @RequestMapping(path = "/totalBalance", method = RequestMethod.GET)
  public ResponseEntity getTotalBalance() {

    return new ResponseEntity(dolores.getTotalBalance(), HttpStatus.OK);
  }
}
