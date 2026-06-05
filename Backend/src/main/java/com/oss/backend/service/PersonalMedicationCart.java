package com.oss.backend.service;

import com.oss.backend.dto.DrugCartRequestDto;
import com.oss.backend.entity.Medicine;
import com.oss.backend.repository.MedicineRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PersonalMedicationCart {

    private final MedicineRepository MedicineRepository;

    public PersonalMedicationCart(MedicineRepository MedicineRepository) {
        this.MedicineRepository = MedicineRepository;
    }

    /**
     * 약품을 개인 보관함에 추가합니다.
     * 
     * @param requestDto 보관함에 추가할 약품 데이터
     * @param username 사용자 계정명
     * @return DB에 저장된 약품 엔티티
     */
    public Medicine addToCart(DrugCartRequestDto requestDto, String username) {
        Medicine item = new Medicine(
                requestDto.getItemName(),
                requestDto.getItemSeq(),
                requestDto.getEfcyQesitm(),
                requestDto.getUseMethodQesitm(),
                requestDto.getAtpnQesitm(),
                requestDto.getSeQesitm(),
                username
        );
        return MedicineRepository.save(item);
    }

    /**
     * 특정 사용자의 보관함 목록을 조회합니다.
     * 
     * @param username 조회할 사용자 계정명
     * @return 사용자가 보관한 약품 리스트
     */
    public List<Medicine> getCartItemsByUsername(String username) {
        return MedicineRepository.findByUsername(username);
    }

    /**
     * 개인 보관함에서 특정 약품을 삭제합니다.
     * 
     * @param id 삭제할 약품 엔티티의 ID
     */
    public void deleteCartItem(Long id) {
        MedicineRepository.deleteById(id);
    }
}
