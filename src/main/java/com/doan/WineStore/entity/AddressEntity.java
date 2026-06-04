package com.doan.WineStore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", length = 20)
    private String type = "home";

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "country", length = 100)
    private String country = "Việt Nam";

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "default_user_guard")
    private Boolean defaultUserGuard;

    public AddressEntity() {}

    public AddressEntity(Long userId, String fullName, String phone, String addressLine1,
                         String addressLine2, String city, String province,
                         String country, String postalCode, String type, Boolean isDefault) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.province = province;
        this.country = country != null ? country : "Việt Nam";
        this.postalCode = postalCode;
        this.type = type;
        this.isDefault = isDefault;
        this.defaultUserGuard = isDefault != null && isDefault ? true : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public Boolean getDefaultUserGuard() { return defaultUserGuard; }
    public void setDefaultUserGuard(Boolean defaultUserGuard) { this.defaultUserGuard = defaultUserGuard; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null && !addressLine1.isBlank()) sb.append(addressLine1).append(", ");
        if (addressLine2 != null && !addressLine2.isBlank()) sb.append(addressLine2).append(", ");
        if (province != null && !province.isBlank()) sb.append(province).append(", ");
        if (city != null && !city.isBlank()) sb.append(city);
        return sb.toString();
    }
}
