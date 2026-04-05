package com.banking.cbs.account.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountMasterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account Product & Account Master API")
                        .description("Core Banking System — Account Product and Account Master Service. " +
                                "Covers account product definition, parameters, interest tiers, charges, " +
                                "and account opening, lifecycle management, balances, earmarks, and ledger.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BankSoft")
                                .email("admin@banksoft.com"))
                        .license(new License()
                                .name("Internal Use Only")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development")
                ));
    }
}
