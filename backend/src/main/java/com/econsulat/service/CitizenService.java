package com.econsulat.service;

import com.econsulat.model.Citizen;
import com.econsulat.model.User;
import com.econsulat.repository.CitizenRepository;
import com.econsulat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Citizen> getAllCitizens() {
        return citizenRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Citizen> getCitizensByUser(Long userId) {
        return citizenRepository.findByUserId(userId);
    }

    public Citizen createCitizen(Citizen citizen) {
        return citizenRepository.save(citizen);
    }

    public Optional<Citizen> getCitizenById(Long id) {
        return citizenRepository.findById(id);
    }

    public Citizen getCitizenByIdOrThrow(Long id) {
        return citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen not found with id: " + id));
    }

    public Citizen updateCitizen(Long id, Citizen citizenDetails) {
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        citizen.setFirstName(citizenDetails.getFirstName());
        citizen.setLastName(citizenDetails.getLastName());
        citizen.setBirthDate(citizenDetails.getBirthDate());
        citizen.setBirthPlace(citizenDetails.getBirthPlace());
        citizen.setDocumentType(citizenDetails.getDocumentType());
        citizen.setStatus(citizenDetails.getStatus());

        return citizenRepository.save(citizen);
    }

    public void deleteCitizen(Long id) {
        citizenRepository.deleteById(id);
    }

    public long getTotalCitizens() {
        return citizenRepository.count();
    }

    public long getTotalCitizensByUser(Long userId) {
        return citizenRepository.findByUserId(userId).size();
    }
}