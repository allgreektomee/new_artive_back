package com.artivefor.me.data.reference;


import com.artivefor.me.data.common.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor
public class ReferenceTranslation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 자료를 왜 참고했는지, 어떤 영감을 받았는지에 대한 설명
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC; // PUBLIC, PRIVATE, FOLLOWERS_ONLY 등

    // 자동 번역 여부
    private boolean isAutomated = false;

    public ReferenceTranslation(String description) {
        this.description = description;
    }
}