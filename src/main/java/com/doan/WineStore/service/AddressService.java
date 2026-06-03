package com.doan.WineStore.service;

import com.doan.WineStore.entity.AddressEntity;
import com.doan.WineStore.repository.AddressRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<AddressEntity> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }

    @Transactional
    public AddressEntity save(Long userId, String fullName, String phone,
                              String addressLine1, String addressLine2,
                              String city, String province,
                              String country, String postalCode,
                              String type, Boolean isDefault) {
        try {
            if (isDefault != null && isDefault) {
                clearOtherDefaults(userId);
            }
            AddressEntity addr = new AddressEntity(userId, fullName, phone, addressLine1,
                    addressLine2, city, province, country, postalCode, type,
                    isDefault != null && isDefault);
            AddressEntity saved = addressRepository.save(addr);
            entityManager.flush();
            log.info("Saved address id={} for userId={}", saved.getId(), userId);
            return saved;
        } catch (Exception e) {
            log.error("Save address failed for userId={}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public AddressEntity update(Long id, Long userId, String fullName, String phone,
                                String addressLine1, String addressLine2,
                                String city, String province,
                                String country, String postalCode,
                                String type, Boolean isDefault) {
        try {
            AddressEntity addr = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
            if (addr == null) {
                log.warn("Update failed: address id={} not found or not owned by userId={}", id, userId);
                return null;
            }

            if (isDefault != null && isDefault) {
                clearOtherDefaults(userId);
            }

            addr.setFullName(fullName);
            addr.setPhone(phone);
            addr.setAddressLine1(addressLine1);
            addr.setAddressLine2(addressLine2);
            addr.setCity(city);
            addr.setProvince(province);
            addr.setCountry(country != null ? country : "Việt Nam");
            addr.setPostalCode(postalCode);
            addr.setType(type);
            addr.setIsDefault(isDefault != null && isDefault);
            AddressEntity saved = addressRepository.save(addr);
            entityManager.flush();
            log.info("Updated address id={} for userId={} (success)", id, userId);
            return saved;
        } catch (Exception e) {
            log.error("Update address id={} failed for userId={}: {}", id, userId, e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    public boolean delete(Long id, Long userId) {
        try {
            AddressEntity addr = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
            if (addr == null) {
                log.warn("Delete failed: address id={} not found or not owned by userId={}", id, userId);
                return false;
            }
            addr.setDeletedAt(LocalDateTime.now());
            addressRepository.save(addr);
            entityManager.flush();
            log.info("Soft-deleted address id={} for userId={}", id, userId);
            return true;
        } catch (Exception e) {
            log.error("Delete address id={} failed for userId={}: {}", id, userId, e.getMessage(), e);
            return false;
        }
    }

    private void clearOtherDefaults(Long userId) {
        addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .forEach(a -> { a.setIsDefault(false); addressRepository.save(a); });
    }
}
