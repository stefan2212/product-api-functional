package com.wirebraincoffee.productapifunctional.configuration;

import com.wirebraincoffee.productapifunctional.handler.ProductHandler;
import com.wirebraincoffee.productapifunctional.router.ProductRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebConfiguration {

    @Bean
    RouterFunction<ServerResponse> routeProduct(ProductHandler productHandler) {
        return ProductRouter.routeProducts(productHandler);
    }
}
