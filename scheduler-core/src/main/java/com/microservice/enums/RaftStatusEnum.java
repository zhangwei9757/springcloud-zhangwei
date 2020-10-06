package com.microservice.enums;

/**
 * @author zw
 * @date 2020-10-02
 * <p> raft 算法状态枚举
 * leader（领导者），follower（跟随者），candidate（候选人)
 *
 * <p> raft算法解释:
 * 1. 所有节点启动时都是follower状态；
 * 2. 在一段时间内如果没有收到来自leader的心跳，从follower切换到candidate，发起选举；
 * 3. 如果收到majority的造成票（含自己的一票）则切换到leader状态；
 * 4. 如果发现其他节点比自己更新，则主动切换到follower。
 *
 * <p> 实现机制
 * leader会不停的给follower发心跳消息，表明自己的存活状态。如果leader故障，那么follower会转换成candidate，重新选出leader。
 *
 * <p> 选举过程详解
 * 1. 收到majority的投票（含自己的一票），则赢得选举，成为leader
 * 2. 被告知别人已当选，那么自行切换到follower
 * 3. 一段时间内没有收到majority投票，则保持candidate状态，重新发出选举
 */
public enum RaftStatusEnum {
    /**
     * leader（领导者）
     */
    LEADER("leader", "领导者"),
    /**
     * follower（跟随者）
     */
    FOLLOWER("follower", "跟随者"),
    /**
     * candidate（候选人)
     */
    CANDIDATE("candidate", "候选人");

    private String name;
    private String desc;

    RaftStatusEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
