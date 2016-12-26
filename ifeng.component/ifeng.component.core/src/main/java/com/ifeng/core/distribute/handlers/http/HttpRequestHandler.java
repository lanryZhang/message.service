/*
* RequestHandler.java 
* Created on  202016/12/14 15:14 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.distribute.handlers.http;

import com.alibaba.fastjson.JSONObject;
import com.ifeng.configurable.Configurable;
import com.ifeng.configurable.Context;
import com.ifeng.core.Dispatcher;
import com.ifeng.core.distribute.constances.ContextConstances;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.*;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> implements Configurable {

    private HttpRequest request;
    private HttpPostRequestDecoder decoder;
    private Dispatcher dispatcher;
    private Context context = new Context();

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(false);

    public void messageReceived(ChannelHandlerContext ctx, HttpObject message) {
        if (message instanceof HttpRequest) {
            request = (HttpRequest) message;

            context.put("request", request);


            if (request.method() == HttpMethod.GET) {

                parsePostParameters(request.uri());
                ResponseModel res = (ResponseModel) dispatcher.dispatch(context);

                writeResponse(ctx.channel(),res);

            } else if (request.method() == HttpMethod.POST) {
                decoder = new HttpPostRequestDecoder(factory,request);

                HttpContent chunk = (HttpContent) message;

                if (chunk instanceof LastHttpContent){
                    ByteBuf buf = chunk.content();
                    String data = buf.toString(CharsetUtil.UTF_8);
                    String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
                    if ((null != contentType) && (contentType.toLowerCase().startsWith("application/json") ||
                        contentType.toLowerCase().startsWith("application/x-www-form-urlencoded"))){
                        Object object = JSONObject.parse(data);

                        if (object != null){
                            context.put("data",object);
                        }
                    }else{
                        data = request.uri()+"?"+data;
                        parsePostParameters(data);
                    }

                    ResponseModel res = (ResponseModel) dispatcher.dispatch(context);

                    writeResponse(ctx.channel(),res);
                }
            }
        }
    }

    private void parsePostParameters(String uri){
        QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);

        String key = StringUtils.strip(decoderQuery.path()+"/$" + request.method(),"/");
        context.put(ContextConstances.PROCESSOR_KEY,key);

        Map<String, List<String>> uriAttributes = decoderQuery.parameters();
        for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
            for (String attrVal : attr.getValue()) {
                context.put(attr.getKey(),attrVal);
            }
        }
    }
    /**
     * http返回响应数据
     *
     * @param channel
     */
    private void writeResponse(Channel channel,ResponseModel model) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(model.getContent().toString(), CharsetUtil.UTF_8);

        // Decide whether to close the connection or not.
        boolean close = request.headers().contains(CONNECTION, HttpHeaderValues.CLOSE, true)
                || request.protocolVersion().equals(HttpVersion.HTTP_1_0)
                && !request.headers().contains(CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, model.getStatus(), buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH, buf.readableBytes());

        // Write the response.

        // Close the connection after the write operation is done if necessary.
        if (close) {
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }else{
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            channel.writeAndFlush(response);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
            throws Exception {
        messageReceived(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
       // ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void config(Context context) {

    }
}
