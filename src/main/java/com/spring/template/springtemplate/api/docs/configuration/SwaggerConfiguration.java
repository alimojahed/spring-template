package com.spring.template.springtemplate.api.docs.configuration;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;


/**
 * @author Ali Mojahed on 2/14/2021
 * @project spring-template
 **/


@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${server.version.code}")
    private String SERVER_VERSION_CODE;

    @Value("${server.version.name}")
    private String SERVER_VERSION_NAME;

    @Value("${swagger.title}")
    private String SWAGGER_TITLE;

    @Value("${swagger.terms-of-service}")
    private String SWAGGER_TERMS_OF_SERVICE;

    @Value("${swagger.description}")
    private String SWAGGER_DESCRIPTION;

    @Value("${swagger.redirect-url}")
    private String SWAGGER_REDIRECT_URL;

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(PathSelectors.regex("/error.*").negate()/*Predicates.not(PathSelectors.regex("/error.*")*/)
                .build()
                .securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(securityContexts())
                .apiInfo(apiEndPointsInfo());

    }

    private ApiInfo apiEndPointsInfo() {

        return new ApiInfoBuilder()
                .title(SWAGGER_TITLE)
                .description(SWAGGER_DESCRIPTION)
                .termsOfServiceUrl(SWAGGER_TERMS_OF_SERVICE)
                .version(SERVER_VERSION_CODE)
                .build();

    }

    private List<SecurityContext> securityContexts() {

        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build()
        );

    }

    private List<SecurityReference> defaultAuth() {
//        final AuthorizationScope readScope = new AuthorizationScope("storage", "read files");
//        final AuthorizationScope writeScope = new AuthorizationScope("storage:write", "write files");
//        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{readScope, writeScope};
//        return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));

        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];

        authorizationScopes[0] = authorizationScope;

        return Collections.singletonList(new SecurityReference("apiToken", authorizationScopes));

    }

    private ApiKey apiKey() {
        return new ApiKey("apiToken", "apiToken", "header");
    }


    @Bean
    public UiConfiguration uiConfig() {

        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .docExpansion(DocExpansion.LIST)
                .filter(true)
                .build();

    }

    @Bean
    SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
//                .clientId("test-app-client-id")
//                .clientSecret("test-app-client-secret")
//                .realm("test-app-realm")
//                .appName("test-app")
//                .scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .enableCsrfSupport(false)
                .build();
    }

    @ApiIgnore
    @RestController
    public static class Home {
        @Value("${swagger.redirect-url}")
        private String SWAGGER_REDIRECT_URL;

        @GetMapping("/docs")
        public ModelAndView help(ModelMap model, HttpServletRequest request) {

            String sharp = request.getServletPath().substring(5);
            String redirect;

            if (StringUtils.isNotEmpty(sharp) && !sharp.equals("/")) {
                redirect = "redirect:" + SWAGGER_REDIRECT_URL + "#" + sharp;
            } else {
                redirect = "redirect:" + SWAGGER_REDIRECT_URL;
            }

            return new ModelAndView(redirect, model);

        }

    }


}
