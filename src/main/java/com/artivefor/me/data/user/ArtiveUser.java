package com.artivefor.me.data.user;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// ⭐️ UserDetails 인터페이스를 추가하여 시큐리티와 호환시킵니다.
public class ArtiveUser extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String slug;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings settings;

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Artwork> artworks = new ArrayList<>();

    // ================= UserDetails 구현 메서드 ================= //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role 정보를 시큐리티 권한으로 변환 (예: "ROLE_USER")
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email; // 로그인 아이디로 email 사용
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}