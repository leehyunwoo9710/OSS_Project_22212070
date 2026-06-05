package com.oss.backend.controller;

import com.oss.backend.dto.DrugCartRequestDto;
import com.oss.backend.entity.Medicine;
import com.oss.backend.service.PersonalMedicationCart;
import java.util.List;
import org.springframework.http.ResponseEntity;
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

    /**
     * 약 바구니 추가 API.
     * 사용자가 선택한 약품을 개인 보관함(Cart)에 저장합니다.
     * 
     * @param requestDto 추가할 약품 정보 DTO
     * @param username 사용자 계정명 (헤더에서 추출)
     * @return 저장된 약품(Medicine) 엔티티 정보
     */
    @PostMapping("/add")
    public ResponseEntity<Medicine> addToCart(
            @RequestBody DrugCartRequestDto requestDto,
            @RequestHeader("X-Username") String username) {
        Medicine savedItem = PersonalMedicationCart.addToCart(requestDto, username);
        return ResponseEntity.ok(savedItem);
    }

    /**
     * 개인 약 바구니 목록 조회 API.
     * 특정 사용자가 보관함에 담아둔 모든 약품 목록을 반환합니다.
     * 
     * @param username 조회할 사용자 계정명 (헤더에서 추출)
     * @return 사용자의 저장된 약품 리스트
     */
    @GetMapping("/list")
    public ResponseEntity<List<Medicine>> getCartList(@RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(PersonalMedicationCart.getCartItemsByUsername(username));
    }

    /**
     * 약 바구니 아이템 삭제 API.
     * 사용자의 약 보관함에서 특정 약품을 삭제합니다.
     * 
     * @param id 삭제할 약품(Medicine) 엔티티의 고유 ID
     * @return 처리에 성공할 경우 HTTP 204 No Content 반환
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        PersonalMedicationCart.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }
}
