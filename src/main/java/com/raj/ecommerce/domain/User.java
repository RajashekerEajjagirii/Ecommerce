package com.raj.ecommerce.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;
    @NotBlank(message = "name should not be empty or null")
    @ToString.Include
    private String username;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "email should not be empty or null")
    @ToString.Include
    private String email;
    @Column(nullable = false)
    @NotBlank(message = "password should not be empty or null")
    private String password;
    @NotBlank(message = "mobile number should not be empty or null")
    private String mobileNumber;
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")

    )
    private Set<Role> roles=new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Address> addresses = new HashSet<>();
    private boolean isVerified;
    private int otp;
}

