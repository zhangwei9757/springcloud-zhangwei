package com.microservice.scheduler;

import com.microservice.bean.ExecutorJobMessage;
import com.microservice.bean.RedisMessage;
import com.microservice.bean.SchedulerConfigurationProperties;
import com.microservice.bean.SyncNotifyMessage;
import com.microservice.enums.RedisListenerEnum;
import com.microservice.proto.MessageProtocolPoJo;
import com.microservice.protocol.BaseProtocol;
import com.microservice.protocol.DeregisterRequest;
import com.microservice.redis.RedisDefaultClientHandler;
import com.microservice.utils.JsonUtils;
import com.microservice.utils.PingPongUtils;
import com.microservice.utils.ProtoUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author zhangwei
 * @date 2020-09-19
 * <p>
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class ExecutorGroupServerHandler extends SimpleChannelInboundHandler<MessageProtocolPoJo.MessageProtocol> {

    public final static Map<String, Channel> ALL_CHANNELS = new HashMap<>();

    public final static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DeregisterRequest deregisterRequest;

    @Resource
    private SchedulerConfigurationProperties properties;

    @Resource
    private RedisDefaultClientHandler defaultClientHandler;

    @Resource
    private ExecutorGroupServer groupServer;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        CHANNEL_GROUP.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + LocalDateTime.now() + " \n");
        // CHANNEL_GROUP.add(channel);
        // ALL_CHANNELS.put(channel.remoteAddress().toString(), channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        CHANNEL_GROUP.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了\n");
        log.info(">>> channelGroup size: [{}]", CHANNEL_GROUP.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(">>> channelActive {} 上线了~", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(">>> channelInactive {} 离线了~", ctx.channel().remoteAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocolPoJo.MessageProtocol messageProtocol) throws Exception {
        Channel channel = ctx.channel();

        /*
            1. 协议消息        => ExecutorJobMessage
            2. ping pong 消息 => ping pong
            3. 普通字符串消息   => String
         */
        String protocolType = messageProtocol.getProtocol();

        if (Objects.nonNull(protocolType) && StringUtils.isNotBlank(protocolType)) {
            // 1. 存在协议，使用协议处理机制
            BaseProtocol protocol = ProtoUtils.protocol(protocolType, applicationContext);
            protocol.process(messageProtocol, channel);

            // 2. 协议消息必须转发
            RedisMessage leader = groupServer.getLeader();
            if (Objects.nonNull(leader)) {
                String content = messageProtocol.getContent();
                ExecutorJobMessage jobMessage = JsonUtils.fromJson(content, ExecutorJobMessage.class);

                SyncNotifyMessage notifyMessage = new SyncNotifyMessage();
                notifyMessage.setJobMessage(jobMessage)
                        .setClusterMemberVoteKey(groupServer.getCurrentServer().getVoteKey())
                        .setClusterMemberHashCode(channel.hashCode())
                        .setChannelKey(findName(channel));
                String json = JsonUtils.toJson(notifyMessage);
                this.push(json);
            }
        } else {
            // 1. 不存在协议，暂时使用客户端组转发机制
            String content = messageProtocol.getContent();

            CHANNEL_GROUP.forEach(ch -> {
                String msg = channel == ch ?
                        String.format("自己: %s, 消息: %s", channel.remoteAddress(), content) :
                        String.format("客户端: %s, 消息: %s", channel.remoteAddress(), content);

                MessageProtocolPoJo.MessageProtocol message = MessageProtocolPoJo.MessageProtocol
                        .newBuilder()
                        .setLen(msg.getBytes(PingPongUtils.CHARSET).length)
                        .setContent(msg)
                        .build();
                // 2. 通知自己名下所有客户端
                ch.writeAndFlush(message);
            });

            if (!Objects.deepEquals(content, PingPongUtils.PONG)) {
                // 3. 只有普通消息中的非 ping pong 消息才通过集群转发
                RedisMessage leader = groupServer.getLeader();
                if (Objects.nonNull(leader)) {
                    SyncNotifyMessage notifyMessage = new SyncNotifyMessage();
                    notifyMessage.setCommonlyMessage(content)
                            .setClusterMemberVoteKey(groupServer.getCurrentServer().getVoteKey())
                            .setClusterMemberHashCode(channel.hashCode())
                            .setChannelKey(findName(channel));
                    String json = JsonUtils.toJson(notifyMessage);
                    this.push(json);
                }
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(">>> exceptionCaught, {}", cause.getLocalizedMessage());
        ctx.close();
    }

    /**
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            IdleStateEvent event = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            switch (event.state()) {
                case READER_IDLE:
                    // 1. 读超时默认不处理
                case WRITER_IDLE:
                    // 2. 写超时默认不处理
                    this.ping(channel);
                    break;
                case ALL_IDLE:
                    // 3. 如果所有动作都超时，此时心跳机制触发
                    log.info(">>> userEventTriggered 读写超时, 与客户端自动断开链接...");
                    // 4. 一旦断开，先清除集群或者客户端列表信息, 再断开连接, 否则会导致意想不到的错误, 切记切记...
                    deregisterRequest.onProcess(null, channel);
                    ctx.disconnect();
                    return;
                default:
                    break;
            }
        }
    }

    private void push(String message) {
        // 广播给其它集群成员服务，通知其名下所有已连接的客户端 1.协议消息 2.普通消息 3.ping pong 已过滤掉
        defaultClientHandler.eventPush(RedisListenerEnum.SYNC_CLIENT_MESSAGE, message);
    }

    private void ping(Channel channel) {
        channel.eventLoop().schedule(() -> {
            MessageProtocolPoJo.MessageProtocol ping = MessageProtocolPoJo.MessageProtocol
                    .newBuilder()
                    .setLen(PingPongUtils.PING.getBytes(PingPongUtils.CHARSET).length)
                    .setContent(PingPongUtils.PING).build();
            channel.writeAndFlush(ping);
        }, properties.getPing(), TimeUnit.SECONDS);
    }

    public static String findName(Channel channel) {
        if (!CollectionUtils.isEmpty(ALL_CHANNELS)) {
            return ALL_CHANNELS.entrySet()
                    .stream()
                    .filter(f -> f.getValue() == channel)
                    .map(Map.Entry::getKey)
                    .findFirst().orElseGet(null);
        }
        return null;
    }

    public static Channel findChannel(String name) {
        if (!CollectionUtils.isEmpty(ALL_CHANNELS)) {
            return ALL_CHANNELS.entrySet()
                    .stream()
                    .filter(f -> Objects.deepEquals(f.getKey(), name))
                    .map(Map.Entry::getValue)
                    .findFirst().orElseGet(null);
        }
        return null;
    }
}
