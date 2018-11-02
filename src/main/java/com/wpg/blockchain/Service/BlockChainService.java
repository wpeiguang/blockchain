package com.wpg.blockchain.Service;

import com.alibaba.fastjson.JSON;
import com.wpg.blockchain.model.Block;
import com.wpg.blockchain.util.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockChainService {

    /**
     * 区块链
     */
    private List<Block> blockChain;

    public BlockChainService(){
        this.blockChain = new ArrayList<Block>();
        blockChain.add(this.getGenesisBlock());
    }

    private Block getGenesisBlock(){
        Block newBlock = new Block();
        calculateNonceHash(1, "0", 1531339156921l,"Genesis Block",0,newBlock);
        return newBlock;
    }

    /**
     * 计算nonce值
     * @param index
     * @param previousHash
     * @param timestamp
     * @param data
     * @param nonce
     * @param newBlock
     * @return
     */
    private String calculateNonceHash(int index, String previousHash, long timestamp, String data, long nonce, Block newBlock){
        String hash;
        do {
            nonce = nonce + 1;
            System.out.println("nonce:"+nonce);
            StringBuilder builder = new StringBuilder(index).append(previousHash).append(timestamp).append(data).append(nonce);
            hash = CryptoUtil.getSHA256(builder.toString());
            if(isValidHashDifficulty(hash)) {
                newBlock.setData(data);
                newBlock.setHash(hash);
                newBlock.setIndex(index);
                newBlock.setNonce(nonce);
                newBlock.setPreviousHash(previousHash);
                newBlock.setTimestamp(timestamp);
            }
        }while (!isValidHashDifficulty(hash));
        return hash;
    }

    /**
     * 检测是否符合难度要求，挖矿操作
     * @param hash
     * @return
     */
    private boolean isValidHashDifficulty(String hash) {
        int dificutty = 4;
        char zero = '0';
        int i;
        for(i = 0; i < hash.length(); i++) {
            char ichar = hash.charAt(i);
            if(ichar != zero) {
                break;
            }
        }
        return i >= dificutty;
    }

    /**
     * 生成新区块
     * @param blockData
     * @return
     */
    public Block generateNextBlock(String blockData){
        //获得前一个区块
        Block previousBlock = this.getLatestBlock();
        Block newBlock = new Block();
        //区块的索引加1
        int nextIndex = previousBlock.getIndex() + 1;
        //现在的时间戳
        long nextTimestamp = System.currentTimeMillis();
        //最后一个区块的难度值
//        Block latestBlock = getLatestBlock();
//        long nonce = latestBlock.getNonce();
        calculateNonceHash(nextIndex, previousBlock.getHash(), nextTimestamp, blockData, 0, newBlock);
        System.out.println("new block: " + JSON.toJSONString(newBlock));
        return newBlock;
    }

    /**
     * 获取最后一个区块
     * @return 返回区块内容
     */
    public Block getLatestBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    /**
     * 添加区块
     * @param newBlock
     */
    public void addBlock(Block newBlock) {
        if (isValidNewBlock(newBlock, getLatestBlock())) {
            blockChain.add(newBlock);
        }
    }

    /**
     * 检查区块是否新区块
     * @param newBlock
     * @param previousBlock
     * @return true/false
     */
    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
            //前一个区块的索引加1不等于新区块
            System.err.println(newBlock.getIndex());
            System.out.println("invalid index");
            return false;
        } else if (!previousBlock.getHash().equals(newBlock.getPreviousHash())) {
            //前一个区块的hash不等于新区块中存储的前一个区块的hash
            System.out.println("invalid previous hash");
            return false;
        } else {
            // String hash = calculateHash(newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(),
            // newBlock.getData());
            String hash = calculateNonceHash(newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData(),newBlock.getNonce(),newBlock);
            //如果hash和计算后的hash不相等
            if (!hash.equals(newBlock.getHash())) {
                System.out.println("invalid hash: " + hash + " " + newBlock.getHash());
                return false;
            }
        } return true;
    }

    /**
     * 新区块链替换成+1的区块链
     * @param newBlocks
     */
    public void replaceChain(List<Block> newBlocks) {
        //如果新区块链合法并且新区块链的长度大于现有区块链的长度，则替换
        if (isValidBlocks(newBlocks) && newBlocks.size() > blockChain.size()) {
            blockChain = newBlocks;
        } else {
            System.out.println("Received blockchain invalid");
        }
    }

    /**
     * 验证区块链是否合法
     * @param newBlocks
     * @return true/false
     */
    private boolean isValidBlocks(List<Block> newBlocks) {
        //获得第一个区块链
        Block fristBlock = newBlocks.get(0);
        //判断第一个区块是否是创世区块
        if (fristBlock.equals(getGenesisBlock())) {
            return false;
        }
        //循环每个验证区块是否合法
        for (int i = 0; i < newBlocks.size(); i++) {
            if (isValidNewBlock(newBlocks.get(i), fristBlock)) {
                fristBlock = newBlocks.get(i);
            } else {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }
}
