package com.oss.backend.controller;

import com.oss.backend.dto.DrugCartRequestDto;
import com.oss.backend.entity.Medicine;
import com.oss.backend.service.PersonalMedicationCart;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/cart")
public class DrugCartController {

    private final PersonalMedicationCart PersonalMedicationCart;

    public DrugCartController(PersonalMedicationCart PersonalMedicationCart) {
        this.PersonalMedicationCart = PersonalMedicationCart;
    }

    @PostMapping("/add")
    public ResponseEntity<Medicine> addToCart(
            @RequestBody DrugCartRequestDto requestDto,
            @RequestHeader("X-Username") String username) {
        Medicine savedItem = PersonalMedicationCart.addToCart(requestDto, username);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Medicine>> getCartList(@RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(PersonalMedicationCart.getCartItemsByUsername(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        PersonalMedicationCart.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }
}
