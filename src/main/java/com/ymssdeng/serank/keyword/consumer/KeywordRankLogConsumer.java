package com.ymssdeng.serank.keyword.consumer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ymssdeng.serank.keyword.Keyword;
import com.ymssdeng.serank.keyword.KeywordRank;

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
	public <T extends Keyword> void consume(Keyword keyword,
			List<KeywordRank> krs) {
		// TODO Auto-generated method stub

	}

}
