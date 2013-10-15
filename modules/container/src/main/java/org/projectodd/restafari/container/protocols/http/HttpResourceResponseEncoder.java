package org.projectodd.restafari.container.protocols.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.projectodd.restafari.container.codec.ResourceCodec;
import org.projectodd.restafari.container.codec.ResourceCodecManager;
import org.projectodd.restafari.container.responses.ResourceResponse;

import java.util.List;

public class HttpResourceResponseEncoder extends MessageToMessageEncoder<ResourceResponse> {

    public HttpResourceResponseEncoder(ResourceCodecManager codecManager) {
        this.codecManager = codecManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ResourceResponse msg, List<Object> out) throws Exception {
        HttpResponseStatus status = HttpResponseStatus.OK;
        switch (msg.responseType()) {
            case CREATED:
                status = HttpResponseStatus.CREATED;
                break;
            case READ:
                status = HttpResponseStatus.OK;
                break;
            case UPDATED:
                status = HttpResponseStatus.OK;
                break;
            case DELETED:
                status = HttpResponseStatus.OK;
                break;
        }

        ResourceCodec codec = this.codecManager.getResourceCodec( msg.mimeType() );
        ByteBuf encoded = codec.encode(msg.resource());

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, encoded );
        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, encoded.readableBytes());
        response.headers().add(HttpHeaders.Names.CONTENT_TYPE, msg.mimeType() );
        out.add(response);
    }

    private ResourceCodecManager codecManager;
}
