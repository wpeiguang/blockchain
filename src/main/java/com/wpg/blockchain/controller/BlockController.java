package com.wpg.blockchain.controller;

import com.alibaba.fastjson.JSON;
import com.wpg.blockchain.Service.BlockChainService;
import com.wpg.blockchain.Service.P2PService;
import com.wpg.blockchain.model.Block;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class BlockController {

    @Autowired
    private ServletContext servletContext;

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(){
        return "Hello World";
    }

    @RequestMapping(value = "blocks", method = RequestMethod.GET)
    public String blocks(){
        P2PService p2PService = (P2PService) servletContext.getAttribute("p2pserver");
        BlockChainService blockChainService = p2PService.getBlockChainService();
        return JSON.toJSONString(blockChainService.getBlockChain());
    }

    @RequestMapping(value = "mineBlock", method = RequestMethod.POST)
    public String mimeBlock(HttpServletRequest request){
        P2PService p2PService = (P2PService) servletContext.getAttribute("p2pserver");
        BlockChainService blockChainService = p2PService.getBlockChainService();
        String data = request.getParameter("data");
        Block newBlock = blockChainService.generateNextBlock(data);
        blockChainService.addBlock(newBlock);
        p2PService.broadcast(p2PService.responseLatestMsg());
        String s = JSON.toJSONString(newBlock);
        return s;
    }

    @RequestMapping(value = "addPeer", method = RequestMethod.POST)
    public String addPeer(HttpServletRequest request){
        P2PService p2PService = (P2PService) servletContext.getAttribute("p2pserver");
        String peer = request.getParameter("peer");
        String hostAdd = peer.split(":")[1].split("//")[1];
        for(WebSocket socket : p2PService.getSockets()){
            InetSocketAddress remoteSocketAddr = socket.getRemoteSocketAddress();
            if(remoteSocketAddr.getAddress().equals(hostAdd)){
                return "peer is already connect";
            }
        }
        p2PService.connectToPeer(peer);
        return "success";
    }

    @RequestMapping(value = "peers", method = RequestMethod.GET)
    public String peers(){
        List<Map<String, String>> peerList = new ArrayList<Map<String, String>>();
        P2PService p2PService = (P2PService) servletContext.getAttribute("p2pserver");
        for(WebSocket socket : p2PService.getSockets()){
            InetSocketAddress remoteSocketAddr = socket.getRemoteSocketAddress();
            Map<String, String> peerMap = new HashMap<String, String>();
            peerMap.put("remoteHost", remoteSocketAddr.getHostName());
            peerMap.put("remotePort", remoteSocketAddr.getPort() + "");
            peerList.add(peerMap);
        }
        return JSON.toJSONString(peerList);
    }

}
