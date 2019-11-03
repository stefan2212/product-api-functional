package com.wirebraincoffee.productapifunctional.router;

import com.wirebraincoffee.productapifunctional.handler.ProductHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class ProductRouter {

    public static RouterFunction<ServerResponse> routeProducts(ProductHandler productHandler) {
        return nest(path("/products"),
                nest(accept(MediaType.APPLICATION_JSON).or(contentType(MediaType.APPLICATION_JSON)).or(accept(MediaType.TEXT_EVENT_STREAM)),
                        route(GET("/"), productHandler::getAllProducts)
                                .andRoute(POST("/"), productHandler::saveProduct)
                                .andRoute(GET("/events"), productHandler::getProductEvents)
                                .andNest(path("/{id}"),
                                        route(GET("/"), productHandler::getProduct)
                                                .andRoute(DELETE("/"), productHandler::deleteProduct)
                                                .andRoute(PUT("/"), productHandler::updateProduct)
                                )
                )
        );
    }
}
