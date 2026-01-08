package com.artivefor.me.data.user;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * [ArtiveUser 엔티티 설명]
 * 서비스 이용자(작가 및 일반 유저)의 정보를 담는 테이블입니다.
 * BaseTimeEntity를 상속받아 가입일(createdAt)과 정보 수정일(updatedAt)을 자동으로 가집니다.
 */

@Entity // JPA가 이 클래스를 DB 테이블과 매핑하도록 선언합니다.
@Getter // 모든 필드의 Getter 메서드를 자동으로 생성합니다 (Lombok).
@Builder
@AllArgsConstructor // Builder를 위해 모든 필드를 가진 생성자 필요
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티를 위해 기본 생성자 필요
public class ArtiveUser extends BaseTimeEntity {

    @Id // 이 필드를 테이블의 기본키(PK)로 지정합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 auto_increment를 사용하여 ID를 자동 생성합니다.
    private Long id;

    @Column(nullable = false, unique = true) // DB 컬럼 설정: 비어있을 수 없고(null 불가), 중복될 수 없습니다.
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String slug; // 작가 개인 페이지 주소 (예: artive.me/my-name)

    @Builder.Default // ✅ 추가: 빌더 사용 시에도 기본값(USER)이 유지되도록 함
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // UserProfile 쪽에 @MapsId가 있으므로 이 연결이 중요합니다.
    // ArtiveUser가 지워지면 연결된 Profile도 함께 지워지도록 설정 (Cascade)
    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings settings;

    @Builder.Default // ✅ 추가: 빌더로 생성 시 리스트가 null이 되지 않게 방지
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Artwork> artworks = new ArrayList<>();


}

/*

@Entity	"이 클래스는 DB 테이블이야!" 라고 JPA에게 알림.
@OneToOne	1:1 관계 설정 (유저 한 명에 프로필 하나).
@OneToMany	1:N 관계 설정 (메인 정보 하나에 한국어, 영어 등 여러 번역본).
@MapsId	부모의 ID를 내 ID로 복사해서 사용 (테이블 구조가 훨씬 깔끔해짐).
@MapKeyColumn	자바의 Map 구조를 DB에 저장할 때, 키값(ko, en)을 저장할 컬럼명 지정.
cascade = CascadeType.ALL	"부모를 저장할 때 자식(번역본)도 같이 저장해줘!" 라는 설정.
orphanRemoval = true	"Map에서 데이터를 지우면 DB에서도 삭제해줘!" 라는 설정.

 */