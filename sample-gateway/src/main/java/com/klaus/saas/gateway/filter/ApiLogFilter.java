package com.klaus.saas.gateway.filter;//package com.ctitc.cloud.gateway.filter;
//
//import com.zschina.base.system.client.ReactiveApiLogClient;
//import com.zschina.base.system.client.ReactiveResourceClient;
//import com.zschina.base.system.client.ReactiveUserClient;
//import com.zschina.base.system.model.ApiLog;
//import com.zschina.base.system.model.Product;
//import com.zschina.base.system.model.Resource;
//import com.zschina.base.system.model.User;
//import com.zschina.saas.common.utils.JsonUtils;
//import io.netty.buffer.ByteBufAllocator;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.reactivestreams.Publisher;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
//import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
//import org.springframework.cloud.gateway.support.BodyInserterContext;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.NettyDataBufferFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ReactiveHttpOutputMessage;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.http.server.reactive.ServerHttpResponse;
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
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;
//
///**
// * @author shilin
// * @since 2020-03-28
// */
//@Component
//@Slf4j
//public class ApiLogFilter extends BaseFilter implements GlobalFilter, Ordered {
//
//	private final ReactiveUserClient reactiveUserClient;
//
//	private final ReactiveApiLogClient reactiveApiLogClient;
//
//	private final ReactiveResourceClient reactiveResourceClient;
//
//	// private ObjectMapper objectMapper = new ObjectMapper();
//
//	private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
//
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//	@Value("${spring.codec.max-in-memory-size}")
//	private DataSize maxInMemory;
//
//	private final ServerCodecConfigurer codecConfigurer;
//
//	public ApiLogFilter(ReactiveUserClient reactiveUserClient, ReactiveApiLogClient reactiveApiLogClient,
//	                    ReactiveResourceClient reactiveResourceClient, ServerCodecConfigurer codecConfigurer) {
//		this.reactiveUserClient = reactiveUserClient;
//		this.reactiveApiLogClient = reactiveApiLogClient;
//		this.reactiveResourceClient = reactiveResourceClient;
//		this.codecConfigurer = codecConfigurer;
//	}
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//		ServerHttpRequest request = exchange.getRequest();
//		ServerHttpResponse response = exchange.getResponse();
//
//		// 接口查询开始时间
//		exchange.getAttributes().put(API_TIME_START, sdf.format(new Date()));
//
//		return chain.filter(exchange.mutate()
//				.request(requestDecorator(exchange))
//				.response(responseDecorate(exchange)).build())
//				.then(Mono.fromRunnable(() -> recordApiLog(exchange)));
//	}
//
//	/**
//	 * Request修饰器，用于读取POST请求body内容，如果需要对body修改，也可在该方法内实现
//	 *
//	 * @param exchange
//	 * @return
//	 */
//	private ServerHttpRequestDecorator requestDecorator(ServerWebExchange exchange) {
//		String contentType = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
//
//		// 文件上传时，不记录requestbody
//		if (StringUtils.isNotEmpty(contentType)
//				&& contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
//
//			return new ServerHttpRequestDecorator(exchange.getRequest());
//		} else {
//			return new ServerHttpRequestDecorator(exchange.getRequest()) {
//				@Override
//				public Flux<DataBuffer> getBody() {
//					Flux<DataBuffer> body = super.getBody();
//
//					StringBuilder sb = new StringBuilder();
//
//					return body.doOnNext(dataBuffer -> sb.append(dataBuffer.toString(StandardCharsets.UTF_8)))
//							.thenMany(Flux.defer(() -> {
//								// request body内容放入exchange
//								exchange.getAttributes().put(ATTR_REQ_BODY, sb.toString());
//								DataBuffer db = dataBufferFactory.allocateBuffer();
//								return Flux.just(db.write(sb.toString().getBytes()));
//							}));
//				}
//			};
//		}
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
//				if (APPLICATION_JSON_VALUE.equals(originalResponseContentType)
//						|| APPLICATION_STREAM_JSON_VALUE.equals(originalResponseContentType)) {
//
//					HttpHeaders httpHeaders = new HttpHeaders();
//
//					httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
//
//					/* spring.codec.max-in-memory-size 属性设置不生效，问题并非spring bug
//					   而是因为这里的ClientResponse是在代码中创建，并非使用spring管理的response
//					   所以自己创建的response是使用default配置，那配置文件中配置的spring.codec.max-in-memory-size自然不生效
//					   需要按照如下方法重新设置max-in-memory-size
//					   百度就是个垃圾场，搜不出任何有用的东西，全部都是复制粘贴，关于这个问题搜出来的全部是错误的
//					   特别感谢：
//					   http://theclouds.io/databufferlimitexception-262144/
//					   https://www.jianshu.com/p/d4efcace5267
//					*/
//					codecConfigurer.defaultCodecs().maxInMemorySize((int) maxInMemory.toBytes());
//					ClientResponse clientResponse = ClientResponse
//							.create(exchange.getResponse().getStatusCode(), codecConfigurer.getReaders())
//							.headers(headers -> headers.putAll(httpHeaders))
//							.body(Flux.from(body)).build();
//
//					Mono<String> modifiedBody = clientResponse.bodyToMono(String.class)
//							.flatMap(originalBody -> {
//								// response body内容放入exchange
//								exchange.getAttributes().put(ATTR_RESP_BODY, originalBody);
//								return Mono.just(originalBody);
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
//	/**
//	 * 记录API请求日志
//	 *
//	 * @param exchange
//	 */
//	private void recordApiLog(ServerWebExchange exchange) {
//		ServerHttpRequest request = exchange.getRequest();
//		String username = request.getHeaders().getFirst("username");
//
//		if (StringUtils.isNotEmpty(username)) {
//			String apiUrl = request.getURI().getPath();
//
//			Mono<User> userMono = reactiveUserClient.getByUsernameFromCache(username);
//			Mono<Resource> resourceMono = reactiveResourceClient.getResource(apiUrl);
//
//			Resource r = new Resource();
//			Product p = new Product();
//			r.setProduct(p);
//
//			userMono.subscribe(user -> resourceMono
//					.defaultIfEmpty(r)
//					.subscribe(resource -> {
//						HttpMethod method = request.getMethod();
//						String param = "";
//
//						switch (method) {
//							case GET:
//								param = JsonUtils.toJson(request.getQueryParams());
//								break;
//							case POST:
//								param = exchange.getAttribute(ATTR_REQ_BODY);
//								break;
//						}
//
//						String respBody = exchange.getAttribute(ATTR_RESP_BODY);
//
//						// 非json返回时，将item填写为content-type
//						String item = exchange.getResponse().getHeaders().getFirst("Content-Type");
//						String code = null;
//						String message = null;
//
//						if (!StringUtils.isEmpty(respBody)) {
//							item = JsonUtils.getNodeAsString(respBody, "item");
//							code = JsonUtils.getNodeAsString(respBody, "code");
//							message = JsonUtils.getNodeAsString(respBody, "message");
//						}
//
//						ApiLog apiLog = ApiLog.builder()
//								.user_id(user.getId())
//								.user_name(username)
//								.user_name_cn(user.getName())
//								.cust_code(user.getCustomer().getCode())
//								.cust_name(user.getCustomer().getName())
//								.product_code(resource.getProduct().getCode())
//								.product_name(resource.getProduct().getName())
//								.api_name(resource.getName())
//								.api_url(apiUrl)
//								.param(param)
//								// .result_item(item)
//								.result_code(code)
//								.result_msg(message)
//								.start_time(exchange.getAttribute(API_TIME_START))
//								.end_time(sdf.format(new Date()))
//								.ip(request.getRemoteAddress().getHostString())
//								.build();
//
//						reactiveApiLogClient.save(apiLog).subscribe();
//					}));
//		}
//	}
//
//	@Override
//	public int getOrder() {
//		// 必须 < -1 否则不会调用 writeWith 方法
//		return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 10;
//	}
//}
