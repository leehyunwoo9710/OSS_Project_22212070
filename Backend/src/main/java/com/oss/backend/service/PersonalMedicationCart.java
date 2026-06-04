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

    public List<Medicine> getCartItemsByUsername(String username) {
        return MedicineRepository.findByUsername(username);
    }

    public void deleteCartItem(Long id) {
        MedicineRepository.deleteById(id);
    }
}
