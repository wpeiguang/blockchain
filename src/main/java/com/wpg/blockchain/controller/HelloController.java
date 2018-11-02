package com.wpg.blockchain.controller;

import com.alibaba.fastjson.JSON;
import com.wpg.blockchain.Service.BlockChainService;
import com.wpg.blockchain.model.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class HelloController {

    private BlockChainService blockChainService = new BlockChainService();


    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(){
        return "Hello World";
    }

    @RequestMapping(value = "blocks", method = RequestMethod.GET)
    public String blocks(){
        return JSON.toJSONString(blockChainService.getBlockChain());
    }

    @RequestMapping(value = "mineBlock", method = RequestMethod.POST)
    public String mimeBlock(HttpServletRequest request){
        String data = request.getParameter("data");
        Block newBlock = blockChainService.generateNextBlock(data);
        blockChainService.addBlock(newBlock);

        return JSON.toJSONString(blockChainService.getBlockChain());
    }
}
