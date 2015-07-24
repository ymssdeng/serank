package com.mdeng.serank.keyword.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mdeng.common.utils.Dates;
import com.mdeng.serank.keyword.KeywordRank;
import com.mdeng.serank.keyword.Rank;

/**
 * Log keyword rank to file
 * 
 * @author Administrator
 *
 */
@Component
public class KeywordRankLogConsumer implements KeywordRankConsumer {

  private Logger appLogger = LoggerFactory.getLogger(KeywordRankLogConsumer.class);
  private Logger rankLogger = LoggerFactory.getLogger("consumer");

  @Override
  public void consume(KeywordRank kr) {
    String date = Dates.formatNow("yyyyMMdd");
    for (Rank r : kr.getRankInfos()) {
      String info =
          String.format("%s_%d_%s_%d_%s", date, kr.getGroup(), kr.getKeyword(), r.getRank(), r.getHost());
      rankLogger.info(info);
    }

  }

}
