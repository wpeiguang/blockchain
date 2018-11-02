package com.wpg.blockchain.model;

/**
 * 区块实体类
 */
public class Block {

    /**
     * 索引
     */
    private int index;

    /**
     * 前一个区块的hash值
     */
    private String previousHash;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 数据
     */
    private String data;

    /**
     * hash值
     */
    private String hash;

    /**
     * 难度值
     */
    private long nonce;

    public Block(){
    }

    public Block(int index, String previousHash, long timestamp, String data, String hash){
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.hash = hash;
    }

    public Block(int index, String previousHash, long timestamp, String data, String hash, long nonce){
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.data = data;
        this.hash = hash;
        this.nonce = nonce;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
}
