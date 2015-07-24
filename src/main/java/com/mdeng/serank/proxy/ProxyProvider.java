package com.mdeng.serank.proxy;

import java.util.List;

import org.apache.http.HttpHost;

/**
 * Proxies provider
 * 
 * @author Administrator
 *
 */
public interface ProxyProvider {
  List<HttpHost> getProxies(int size);
}
