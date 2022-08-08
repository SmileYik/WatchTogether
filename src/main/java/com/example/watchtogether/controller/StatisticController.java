package com.example.watchtogether.controller;

import com.example.watchtogether.util.StatisticHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年08月08日 9:15
 */
@CrossOrigin
@RestController
@RequestMapping("/statistic")
public class StatisticController {
  private static final StatisticHelper statisticHelper = new StatisticHelper();


  @GetMapping
  public StatisticHelper getStatistic() {
    return statisticHelper;
  }
}
