package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.LogInRequest;
import com.raj.ecommerce.dto.LogInResponse;
import com.raj.ecommerce.dto.RegisterRequest;
import com.raj.ecommerce.security.JwtTokenProvider;
import com.raj.ecommerce.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "public-module")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
    }

    @Operation(summary = "signUp", description = "user requesting to register with Our Ecommerce site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "success response",
            content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400",description = "Bad Request")
    })
    @PostMapping("/singup")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request){

        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "signIn", description = "user requesting logIn to Our Ecommerce site")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "success response",
                    content = @Content(schema = @Schema(implementation = LogInResponse.class))),
            @ApiResponse(responseCode = "400",description = "Bad Request"),
            @ApiResponse(responseCode = "500",description = "Service Unavailable")
    })
    @PostMapping("/signIn")
    public ResponseEntity<LogInResponse> logIn(@RequestBody @Valid LogInRequest request){

        return new ResponseEntity<>(authService.login(request),HttpStatus.OK);
    }
}
