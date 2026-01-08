package com.artivefor.me.data.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * [BaseTimeEntity 설명]
 * 1. 역할: 모든 엔티티(User, Artwork, Reference 등)의 공통 필드인 '생성시간, 수정시간'을 자동 관리합니다.
 * 2. 왜 쓰나?: 서비스가 커지면 "이 데이터가 언제 생성됐지?" 혹은 "언제 마지막으로 수정됐지?"를 추적하는 로그가 필수인데, 
 * 이를 모든 엔티티마다 코드로 짜지 않고 이 클래스를 상속(extends)받아 해결합니다.
 */

@Getter
@MappedSuperclass // 이 클래스를 상속받는 자식 클래스들에게 아래 필드들을 컬럼으로 인식하게 합니다.
@EntityListeners(AuditingEntityListener.class) // JPA에게 이 엔티티의 생명주기를 감시하고 자동으로 값을 채우라고 지시합니다.
public abstract class BaseTimeEntity {

    @CreatedDate // 데이터가 생성되어 저장될 때 시간이 자동으로 저장됩니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터의 값이 변경될 때 시간이 자동으로 업데이트됩니다.
    private LocalDateTime updatedAt;
}

/*
* 작성하신 코드의 상단에 있는 import 문들은 스프링 부트와 JPA(Java Persistence API)가 데이터베이스와 소통하고,
자동으로 시간을 기록하기 위해 사용하는 핵심 도구함이라고 보시면 됩니다.

각 패키지가 어떤 역할을 하는지 알기 쉽게 설명해 드릴게요.

1. jakarta.persistence.* (표준 규격)
과거에는 javax.persistence였으나 최신 버전(Spring Boot 3.0 이상)에서는 jakarta로 변경되었습니다.
이 패키지는 **"자바 객체를 어떻게 DB 테이블로 변환할 것인가"**에 대한 표준 규칙을 담고 있습니다.

@EntityListeners: "이 엔티티에서 특정 사건(데이터 추가, 수정 등)이 발생하면 누가 지켜보고 처리할지"를 지정합니다.
 여기서는 AuditingEntityListener가 그 감시자 역할을 합니다.

@MappedSuperclass: "이 클래스는 직접 테이블로 만들지는 않지만, 나를 상속받는 클래스들에게 내 필드(createdAt, updatedAt)를 물려주겠다"는 선언입니다.

2. org.springframework.data.annotation.* (스프링의 기록 도구)
데이터베이스 자체 기능이 아니라, 스프링 프레임워크가 제공하는 편리한 자동화 기능을 위한 것들입니다.

@CreatedDate: 엔티티가 생성되어 처음 저장될 때의 시간을 자동으로 주입해줍니다.

@LastModifiedDate: 엔티티의 내용이 바뀔 때마다 마지막 수정 시간을 자동으로 갱신해줍니다.

3. org.springframework.data.jpa.domain.support.AuditingEntityListener (감시자 본체)
이 클래스가 바로 실질적인 **'시간 기록원'**입니다.
위에서 설명한 @EntityListeners에 이 클래스를 등록함으로써,
데이터가 바뀌는 순간을 포착해 @CreatedDate와 @LastModifiedDate가 붙은 필드에 시간을 채워넣습니다.

4. lombok.Getter (코드 다이어트 도구)
자바에서는 필드 값을 가져오기 위해 getCreatedAt() 같은 메서드를 일일이 만들어야 합니다.
 하지만 이 어노테이션 하나만 붙이면 롬복(Lombok)이라는 라이브러리가 컴파일 시점에 자동으로 Getter 메서드들을 생성해줍니다. 코드가 훨씬 깔끔해지죠.

💡 한눈에 보는 동작 원리
사용자가 save() 명령을 내림.

**AuditingEntityListener**가 감지함.

CreatedDate, **LastModifiedDate**가 붙은 곳에 현재 서버 시간을 입력함.

MappedSuperclass 덕분에 자식 엔티티(Artwork 등)의 컬럼으로 함께 DB에 저장됨.

Getter 덕분에 나중에 서비스 코드에서 artwork.getCreatedAt()으로 시간을 꺼내 쓸 수 있음.
*
* */