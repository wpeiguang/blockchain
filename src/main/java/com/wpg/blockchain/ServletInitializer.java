package com.wpg.blockchain;

import com.wpg.blockchain.Service.BlockChainService;
import com.wpg.blockchain.Service.P2PService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;

public class ServletInitializer extends SpringBootServletInitializer {

	@Autowired
	private ServletContext servletContext;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BlockchainApplication.class);
	}

}
