package co.com.bancolombia.api;

import co.com.bancolombia.api.branch.BranchHandler;
import co.com.bancolombia.api.branch.dto.BranchRequestDTO;
import co.com.bancolombia.api.branch.dto.BranchResponseDTO;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.franchise.FranchiseHandler;
import co.com.bancolombia.api.franchise.dto.FranchiseRequestDTO;
import co.com.bancolombia.api.franchise.dto.FranchiseResponseDTO;
import co.com.bancolombia.api.franchise.dto.TopProductResponseDTO;
import co.com.bancolombia.api.product.ProductHandler;
import co.com.bancolombia.api.product.dto.ProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchises"},
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update franchise name",
                            tags = {"Franchises"},
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/top-products",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getTopProductByFranchiseId",
                    operation = @Operation(
                            operationId = "getTopProductByFranchiseId",
                            summary = "Get product with highest stock per branch in a franchise",
                            tags = {"Franchises"},
                            parameters = @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Top products per branch",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopProductResponseDTO.class)))),
                                    @ApiResponse(responseCode = "400", description = "Franchise not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Create a new branch",
                            tags = {"Branches"},
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created",
                                            content = @Content(schema = @Schema(implementation = BranchResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update branch name",
                            tags = {"Branches"},
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated",
                                            content = @Content(schema = @Schema(implementation = BranchResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create a new product",
                            tags = {"Products"},
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created",
                                            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete product",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            description = "Product ID", schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid ID provided",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{id}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update product stock",
                            tags = {"Products"},
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product stock updated",
                                            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{id}/name",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            tags = {"Products"},
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated",
                                            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            )
    })


    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            FranchiseHandler franchiseHandler,
            BranchHandler branchHandler,
            ProductHandler productHandler
            ) {
        return route(POST("/api/franchises"), franchiseHandler::createFranchise)
                .andRoute(POST("/api/branches"), branchHandler::createBranch)
                .andRoute(POST("/api/products"), productHandler::createProduct)
                .andRoute(DELETE("/api/products/{id}"), productHandler::deleteProduct)
                .andRoute(PATCH("/api/products/{id}/stock"), productHandler::updateProductStock)
                .andRoute(PATCH("/api/products/{id}/name"), productHandler::updateProductName)
                .andRoute(GET("/api/franchises/{franchiseId}/top-products"), franchiseHandler::getTopProductByFranchiseId)
                .andRoute(PATCH("/api/franchises/{id}"), franchiseHandler::updateFranchiseName)
                .andRoute(PATCH("/api/branches/{id}"), branchHandler::updateBranchName);
    }
}
