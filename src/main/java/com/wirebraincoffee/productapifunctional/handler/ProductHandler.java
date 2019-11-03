package com.wirebraincoffee.productapifunctional.handler;

import com.wirebraincoffee.productapifunctional.model.Product;
import com.wirebraincoffee.productapifunctional.model.ProductEvent;
import com.wirebraincoffee.productapifunctional.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {

    @Autowired
    private ProductRepository productRepository;

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        if(!request.queryParam("id").isPresent()) {
            Flux<Product> products = productRepository.findAll();

            return ServerResponse.ok()
                    .contentType(APPLICATION_JSON)
                    .body(products, Product.class);
        } else {
            return getProductQueryParam(request);
        }
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> productMono = productRepository.findById(id);

        return productMono
                .flatMap(product -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(productMono, Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    private Mono<ServerResponse> getProductQueryParam(ServerRequest request) {
        Optional<String> id = request.queryParam("id");
        Mono<Product> productMono = productRepository.findById(id.orElse(null));
        return productMono
                .flatMap(product -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(productMono, Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(product -> ServerResponse.status(HttpStatus.CREATED)
                .contentType(APPLICATION_JSON)
                .body(productRepository.save(product), Product.class));
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> existingProductMono = this.productRepository.findById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.zipWith(existingProductMono, (product, existingProduct) -> new Product(
                existingProduct.getId(), product.getName(), product.getPrice()
        )).flatMap(product -> ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(productRepository.save(product), Product.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable("id");

        return productRepository.findById(id)
                .flatMap(existingProduct -> ServerResponse.ok()
                        .build(productRepository.delete(existingProduct))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteAll() {
        return ServerResponse.ok()
                .build(productRepository.deleteAll());
    }

    public Mono<ServerResponse> getProductEvents(ServerRequest request) {
        Flux<ProductEvent> eventsFlux = Flux.interval(Duration.ofSeconds(1)).map(val -> new ProductEvent(val , "Product Event"));

        return ServerResponse.ok()
                .contentType(TEXT_EVENT_STREAM)
                .body(eventsFlux, ProductEvent.class);
    }
}
