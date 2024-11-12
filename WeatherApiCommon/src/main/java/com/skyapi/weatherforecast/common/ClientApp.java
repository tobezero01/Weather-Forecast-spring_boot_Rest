package com.skyapi.weatherforecast.common;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "client_apps")
public class ClientApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String clientId;

    @Column(nullable = false, length = 100, unique = true)
    private String clientSecret;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AppRole role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(orphanRemoval = true)
    @JoinTable(
            name = "apps_locations",
            joinColumns = {@JoinColumn(name = "app_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "location_code", referencedColumnName = "code")}
    )
    private Location location;

    private boolean trashed;

    @PrePersist
    private void generateClientCredentials() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Kiểm tra và tạo clientId nếu chưa có
        if (clientId == null || clientId.isEmpty()) {
            // Tạo clientId ngẫu nhiên, loại bỏ dấu '-' và cắt chuỗi dài 20 ký tự
            this.clientId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
            System.out.println("ClientId: " + clientId);
        }

        // Kiểm tra và tạo clientSecret nếu chưa có
        if (clientSecret == null || clientSecret.isEmpty()) {
            // Tạo clientSecret ngẫu nhiên
            String rawSecret = UUID.randomUUID().toString().replaceAll("-", "");
            System.out.println("Raw clientSecret: " + rawSecret);

            // Mã hóa clientSecret và lưu vào thuộc tính
            this.clientSecret = passwordEncoder.encode(rawSecret);
        }
    }


    public ClientApp() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }
}
