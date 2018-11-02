package com.wpg.blockchain.Service;

import java.net.InetSocketAddress;

import com.alibaba.fastjson.JSON;
import com.wpg.blockchain.model.Block;
import com.wpg.blockchain.model.Constant;
import com.wpg.blockchain.model.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class P2PService {

    private List<WebSocket> sockets;

    @Autowired
    private BlockChainService blockChainService;

    public P2PService(BlockChainService blockChainService){
        this.blockChainService = blockChainService;
        this.sockets = new ArrayList<WebSocket>();
        initP2PServer(7001);
    }
    /**
     * 初始化websoket服务
     * @param port
     */
    public void initP2PServer(int port) {
        /**
         * webSoketServer 初始化
         */
        final WebSocketServer socket = new WebSocketServer(new InetSocketAddress(port)) {
            /**
             * 连接打开
             */
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                write(webSocket, queryChainLatestMsg());
                sockets.add(webSocket);
            }
            /**
             * 连接关闭
             */
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }
            /**
             * 连接消息
             */
            public void onMessage(WebSocket webSocket, String s) {
                handleMessage(webSocket, s);
            }
            /**
             * 容错
             */
            public void onError(WebSocket webSocket, Exception e) {
                System.out.println("connection failed to peer:" + webSocket.getRemoteSocketAddress());
                sockets.remove(webSocket);
            }
            /**
             * 连接开始
             */
            public void onStart() {

            }
        };
        //soket启动
        socket.start();
        System.out.println("listening websocket p2p port on: " + port);
    }

    public void connectToPeer(String peer) {
        try {
            final WebSocketClient socket = new WebSocketClient(new URI(peer)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                        write(this.getConnection(), queryChainLatestMsg());
                    sockets.add(this.getConnection());
                }
                @Override
                public void onMessage(String s) {
                    handleMessage(this.getConnection(), s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("connection failed");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("connection failed");
                    sockets.remove(this);
                }
            };
            socket.connect();
        } catch (URISyntaxException e) {
            System.out.println("p2p connect is error:" + e.getMessage());
        }
    }

    private String queryChainLatestMsg(){
        return JSON.toJSONString(new Message(Constant.QUERY_LATEST));
    }

    private void write(WebSocket ws, String message) {
        ws.send(message);
    }

    private void handleMessage(WebSocket webSocket, String msg){
        try {
            Message message = JSON.parseObject(msg, Message.class);
            System.out.println("receive message: " + JSON.toJSONString(message));
            switch (message.getType()){
                case Constant.QUERY_LATEST:
                    write(webSocket, responseLatestMsg());
                    break;
                case Constant.QUERY_ALL:
                    write(webSocket, responseChainMsg());
                    break;
                case Constant.RESPONSE_BLOCKCHAIN:
                    handleBlockChainResponse(message.getData());
                    break;
            }
        }catch (Exception e){
            System.err.println("handle message is error, " + e.getMessage());
        }
    }

    public String responseLatestMsg(){
        Block[] blocks = {blockChainService.getLatestBlock()};
        return JSON.toJSONString(new Message(Constant.RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
    }

    private String responseChainMsg(){
        return JSON.toJSONString(new Message(Constant.RESPONSE_BLOCKCHAIN, JSON.toJSONString(blockChainService.getBlockChain())));
    }

    private void handleBlockChainResponse(String message){
        List<Block> receiveBlocks = JSON.parseArray(message, Block.class);
        Collections.sort(receiveBlocks, new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        Block latestReceiveBlock = receiveBlocks.get(receiveBlocks.size() - 1);
        Block latestBlock = blockChainService.getLatestBlock();
        if(latestReceiveBlock.getIndex() > latestBlock.getIndex()){
            if(latestBlock.getHash().equals(latestReceiveBlock.getPreviousHash())){
                System.out.println("hash is equals, append to the blockchain");
                blockChainService.addBlock(latestReceiveBlock);
                broadcast(responseLatestMsg());
            }else if(receiveBlocks.size() == 1){
                System.out.println("receive blocks size is 1, query the chain");
                broadcast(queryAllMsg());
            }else{
                blockChainService.replaceChain(receiveBlocks);
            }
        }else{
            System.out.println("receive blockchain is not longer than my blockchain, do nothing");
        }
    }

    public void broadcast(String message){
        for(WebSocket socket : sockets){
            this.write(socket, message);
        }
    }

    private String queryAllMsg(){
        return JSON.toJSONString(new Message(Constant.QUERY_ALL));
    }

    public List<WebSocket> getSockets() {
        return sockets;
    }
}
