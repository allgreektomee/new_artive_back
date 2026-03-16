# Artive Backend (Spring Boot) - 개발 가이드

이 문서는 Artive 프로젝트의 백엔드 아키텍처와 새로운 "하이브리드 헤드리스" 전략에 대해 설명합니다.

## 1. 핵심 아키텍처: 하이브리드 헤드리스 (Hybrid Headless)

백엔드는 React 클라이언트에게 필요한 핵심 데이터를 제공하는 API 서버 역할을 합니다. 특히, 콘텐츠 관리는 기존의 WordPress를 활용하고, 핵심 데이터(작품 메타데이터, 사용자 정보 등)는 Spring Boot 애플리케이션이 관리하는 **하이브리드** 방식을 채택합니다.

-   **데이터 관리 (Spring Boot)**: 작품의 제목, 썸네일 이미지(S3 URL), 상태, 작가 등 정형화된 **메타데이터**를 DB에 저장하고 관리합니다.
-   **콘텐츠 관리 (WordPress)**: 서식이 복잡한 작품 상세 설명, 본문 이미지 등은 기존의 WordPress 에디터를 통해 작성하고 관리합니다.
-   **연결점**: Spring Boot의 `Artwork` 테이블은 WordPress 본문 주소를 담는 `externalUrl` 필드를 가집니다. 클라이언트는 이 URL을 `iframe` 등으로 로드하여 상세 콘텐츠를 보여줍니다.

## 2. '작품(Artwork)' 기능 개발 워크플로우

1.  **콘텐츠 작성 (WP)**: 관리자가 WordPress에서 작품 상세 페이지를 작성하고 해당 URL을 복사합니다. (예: `https://cms.artivefor.me/atelier/some-artwork/`)
2.  **메타데이터 등록 (Admin)**: React로 만든 어드민 페이지에서 '작품 등록' 폼을 엽니다.
3.  **폼 입력**:
    -   **제목**: 작품의 제목을 입력합니다.
    -   **대표 이미지**: S3 업로더를 통해 썸네일을 업로드하고, 반환된 S3 URL을 사용합니다.
    -   **외부 링크**: 1번에서 복사한 WordPress URL을 붙여넣습니다.
4.  **API 요청**: 어드민은 `POST /api/v1/artworks` API를 호출하여 입력된 메타데이터를 Spring Boot 서버에 저장합니다.

## 3. 주요 API 설계 (Artwork)

### `POST /api/v1/artworks` - 작품 생성

-   **Request DTO**: `ArtworkSaveRequest.java`
    ```java
    public class ArtworkSaveRequest {
        private String title;
        private String thumbnailUrl; // S3 URL
        private String externalUrl;  // WordPress 본문 URL
        private WorkStatus status;
        // ... 기타 메타데이터
    }
    ```
    ```

### `GET /api/v1/artworks` - 작품 목록 조회

-   **Response DTO**: `ArtworkListResponse.java`
    ```java
    public record ArtworkListResponse(
        Long id,
        String title,
        String thumbnailUrl,
        String externalUrl, // ? iframe src로 사용될 주소
        WorkStatus status,
        long totalHistoryCount
    ) {}
    ```

이 구조를 통해 **콘텐츠 관리의 편의성(WordPress)**과 **빠른 목록 조회 및 데이터 관리의 안정성(Spring Boot)** 모두 잡을 수 있습니다.
