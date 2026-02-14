package com.raj.ecommerce.controller;

import com.raj.ecommerce.dto.AddressRequest;
import com.raj.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name="user-module")
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "adding Address", description = "user onboarding Address")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",description = "success response",
            content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400",description = "Bad Request")
    })
    @PostMapping("{userId}/address")
    public ResponseEntity<String> addAddress(@RequestBody @Valid AddressRequest request, @PathVariable Long userId){
        return new ResponseEntity<>(userService.onboardingAddress(request,userId), HttpStatus.CREATED);
    }
}
