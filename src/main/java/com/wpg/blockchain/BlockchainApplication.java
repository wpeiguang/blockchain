package com.wpg.blockchain;

import com.wpg.blockchain.Service.BlockChainService;
import com.wpg.blockchain.Service.P2PService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainApplication.class, args);
	}
}
