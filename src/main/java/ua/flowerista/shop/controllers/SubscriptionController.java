package ua.flowerista.shop.controllers;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ua.flowerista.shop.dto.SubscriptionRequest;
import ua.flowerista.shop.services.SubscriptionService;

@RestController
@RequestMapping("/api/subscription")
@CrossOrigin
@Tag(name = "Subscription controller")
public class SubscriptionController {

	@Autowired
	private SubscriptionService service;

	@PostMapping
	@Operation(summary = "Add email to subscription")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "If email already exists"),
			@ApiResponse(responseCode = "202", description = "If email was accepted") })
	public ResponseEntity<?> addBouqueteToWishList(@RequestBody @Valid SubscriptionRequest request ) {
		if(service.sub(request).equals("Email added")) {
			return ResponseEntity.accepted().body("Email added");
		}
		return ResponseEntity.badRequest().body(service.sub(request));
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private Map<String, String> handleValidationExceptions(
	  MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}
}
