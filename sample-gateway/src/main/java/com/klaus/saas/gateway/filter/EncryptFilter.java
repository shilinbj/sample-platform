package com.klaus.saas.gateway.filter;//package com.ctitc.cloud.gateway.filter;
//
//import com.ctitc.cloud.gateway.secure.EncryptUtils;
//import com.ctitc.cloud.gateway.secure.KeyGenUtils;
//import com.ctitc.cloud.gateway.secure.SecureDataResult;
//import com.ctitc.common.utils.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.reactivestreams.Publisher;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
//import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
//import org.springframework.cloud.gateway.support.BodyInserterContext;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ReactiveHttpOutputMessage;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.util.unit.DataSize;
//import org.springframework.web.reactive.function.BodyInserter;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
///**
// * 针对返回数据需要加密的客户，将response body数据加密返回
// *
// * @author Klaus
// * @since 2021/8/24
// */
//@Component
//@Slf4j
//public class EncryptFilter implements GlobalFilter, Ordered {
//
//	private static final String CUST_CODE = "cust_code";
//
//	@Value("${spring.codec.max-in-memory-size}")
//	private DataSize maxInMemory;
//
//	@Value("${key.public.hbnx}")
//	private String hbnxPubKey;
//
//	private final ServerCodecConfigurer codecConfigurer;
//
//	public EncryptFilter(ServerCodecConfigurer codecConfigurer) {
//		this.codecConfigurer = codecConfigurer;
//	}
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//		String custId = exchange.getAttribute(CUST_CODE);
//		if ("hbnx-sharecap".equals(custId)) {
//			return chain.filter(exchange.mutate().response(responseDecorate(exchange)).build());
//		}
//		return chain.filter(exchange);
//	}
//
//	/**
//	 * Response修饰器，用于读取response body内容，如果需要修改，也可以在该方法内实现
//	 *
//	 * @param exchange
//	 * @return
//	 */
//	private ServerHttpResponseDecorator responseDecorate(ServerWebExchange exchange) {
//
//		return new ServerHttpResponseDecorator(exchange.getResponse()) {
//
//			/**
//			 * @param body
//			 * @return
//			 */
//			@Override
//			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//
//				String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
//				// 只有response content type = json时进行修改
//				if (APPLICATION_JSON_VALUE.equals(originalResponseContentType)) {
//
//					HttpHeaders httpHeaders = new HttpHeaders();
//
//					httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
//
//					codecConfigurer.defaultCodecs().maxInMemorySize((int) maxInMemory.toBytes());
//					ClientResponse clientResponse = ClientResponse
//							.create(exchange.getResponse().getStatusCode(), codecConfigurer.getReaders())
//							.headers(headers -> headers.putAll(httpHeaders))
//							.body(Flux.from(body)).build();
//
//					Mono<String> modifiedBody = clientResponse.bodyToMono(String.class)
//							.flatMap(originalBody -> {
//								String item = JsonUtils.getNodeAsString(originalBody, "item");
//								Integer code = JsonUtils.getNode(originalBody, "code", Integer.class);
//								String message = JsonUtils.getNodeAsString(originalBody, "message");
//								Long total_size = JsonUtils.getNode(originalBody, "total_size", Long.class);
//								Integer page_size = JsonUtils.getNode(originalBody, "page_size", Integer.class);
//								Integer page_num = JsonUtils.getNode(originalBody, "page_num", Integer.class);
//
//								String aesKey = KeyGenUtils.genAESKey();
//
//								SecureDataResult ret = SecureDataResult.builder()
//										.secretKey(EncryptUtils.encryptRSA(aesKey, hbnxPubKey))
//										.ciphertext(EncryptUtils.encryptAES(item, aesKey))
//										.code(code)
//										.message(message)
//										.build();
//
//								if (total_size != null) {
//									ret.setTotal_size(total_size);
//									ret.setPage_size(page_size);
//									ret.setPage_num(page_num);
//								}
//
//								return Mono.just(JsonUtils.toJson(ret));
//							});
//
//					BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
//					CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, exchange.getResponse().getHeaders());
//					return bodyInserter.insert(outputMessage, new BodyInserterContext())
//							.then(Mono.defer(() -> {
//								Flux<DataBuffer> messageBody = outputMessage.getBody();
//								HttpHeaders headers = getDelegate().getHeaders();
//								if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)
//										|| headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
//
//									messageBody = messageBody.doOnNext(data -> headers.setContentLength(data.readableByteCount()));
//								}
//								return getDelegate().writeWith(messageBody);
//							}));
//				} else {
//					return getDelegate().writeWith(body);
//				}
//			}
//
//			@Override
//			public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
//				return writeWith(Flux.from(body).flatMapSequential(p -> p));
//			}
//		};
//	}
//
//	@Override
//	public int getOrder() {
//		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 9;
//	}
//}
