/*
+--------------------------------------------------------------------------
|   Mblog [#RELEASE_VERSION#]
|   ========================================
|   Copyright (c) 2014, 2015 mbox. All Rights Reserved
|   http://www.mtons.com
|
+---------------------------------------------------------------------------
*/
package com.wpg.blockchain.listener;

import com.wpg.blockchain.Service.BlockChainService;
import com.wpg.blockchain.Service.P2PService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * @author wangpeiguang
 *
 */
@Component
public class StartupListener implements InitializingBean, ServletContextAware {

	@Autowired
	private ServletContext servletContext;

	private void initServer(){
		BlockChainService blockChainService = new BlockChainService();
		P2PService p2PService = new P2PService(blockChainService);
		p2PService.initP2PServer(7001);
		servletContext.setAttribute("p2pserver", p2PService);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initServer();
	}

}
