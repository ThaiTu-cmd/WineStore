package com.doan.WineStore.service;

import com.doan.WineStore.entity.AddressEntity;
import com.doan.WineStore.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressRepository addressRepository;

    public List<AddressEntity> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }

    @Transactional
    public AddressEntity save(Long userId, String fullName, String phone,
                              String addressLine1, String addressLine2,
                              String city, String province,
                              String country, String postalCode,
                              String type, Boolean isDefault) {
        if (isDefault != null && isDefault) {
            clearOtherDefaults(userId);
        }
        AddressEntity addr = new AddressEntity(userId, fullName, phone, addressLine1,
                addressLine2, city, province, country, postalCode, type,
                isDefault != null && isDefault);
        AddressEntity saved = addressRepository.save(addr);
        log.info("Saved address id={} for userId={}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public AddressEntity update(Long id, Long userId, String fullName, String phone,
                                String addressLine1, String addressLine2,
                                String city, String province,
                                String country, String postalCode,
                                String type, Boolean isDefault) {
        AddressEntity addr = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
        if (addr == null) {
            log.warn("Update failed: address id={} not found for userId={}", id, userId);
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
        log.info("Updated address id={} for userId={}", id, userId);
        return saved;
    }

    @Transactional
    public boolean delete(Long id, Long userId) {
        AddressEntity addr = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
        if (addr == null) {
            log.warn("Delete failed: address id={} not found for userId={}", id, userId);
            return false;
        }
        addressRepository.delete(addr);
        log.info("Deleted address id={} for userId={}", id, userId);
        return true;
    }

    private void clearOtherDefaults(Long userId) {
        addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .forEach(a -> { a.setIsDefault(false); addressRepository.save(a); });
    }
}
