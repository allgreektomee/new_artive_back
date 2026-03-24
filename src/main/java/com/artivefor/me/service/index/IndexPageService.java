package com.artivefor.me.service.index;


import com.artivefor.me.data.index.IndexItem;
import com.artivefor.me.data.index.IndexPage;
import com.artivefor.me.dto.index.IndexPageRequestDTO;
import com.artivefor.me.dto.index.IndexPageResponseDTO;
import com.artivefor.me.repository.index.IndexPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexPageService {


    private final IndexPageRepository indexPageRepository;

    // 1. 최신 페이지 조회 (기존 로직 유지)
    @Transactional(readOnly = true)
    public IndexPageResponseDTO getLatestIndexPage() {
        IndexPage indexPage = indexPageRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("등록된 인덱스 페이지가 없습니다."));
        return convertToResponseDTO(indexPage);
    }

    // 2. 전체 목록 페이징 조회 (추가)
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<IndexPageResponseDTO> getAllIndexPages(Pageable pageable) {
        return indexPageRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    // 3. issueNo로 검색 조회 (추가)
    // ✅ 외부에는 오직 DTO만 public으로 노출
    public IndexPageResponseDTO getIndexPageByIssueNo(String issueNo) {
        return indexPageRepository.findByIssueNo(issueNo)
                .map(this::convertToResponseDTO) // 내부에서 private하게 처리
                .orElseThrow(() -> new RuntimeException("Not Found"));
    }

    // --- 공통 매핑 로직 분리 (Entity -> DTO) ---
    private IndexPageResponseDTO convertToResponseDTO(IndexPage indexPage) {
        return IndexPageResponseDTO.builder()
                .header(IndexPageResponseDTO.HeaderDto.builder()
                        .issueNo(indexPage.getIssueNo())
                        .bgColor(indexPage.getBackgroundColor())
                        .titleTag(indexPage.getTitleTag())
                        .titleTagColor(indexPage.getTitleTagColor())
                        .mainTitle(indexPage.getMainTitle())
                        .mainTitleColor(indexPage.getMainTitleColor())
                        .description(indexPage.getDescription())
                        .descriptionColor(indexPage.getDescriptionColor())
                        .build())
                .config(IndexPageResponseDTO.ContentsConfigDto.builder()
                        .tag(indexPage.getContentsTag())
                        .tagColor(indexPage.getContentsTagColor())
                        .noColor(indexPage.getContentsNoColor())
                        .titleColor(indexPage.getContentsTitleColor())
                        .subColor(indexPage.getContentsSubColor())
                        .build())
                .items(indexPage.getItems().stream()
                        .map(item -> IndexPageResponseDTO.ItemDto.builder()
                                .no(item.getNo())
                                .subject(item.getSubject())
                                .subSubject(item.getSubSubject())
                                .linkUrl(item.getLinkUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 저장: RequestDto -> Entity 변환
    @Transactional
    public Long saveIndexPage(IndexPageRequestDTO dto) {
        IndexPage indexPage = new IndexPage();

        dto.getItems().forEach(itemDto -> {
            IndexItem item = new IndexItem();
            item.setNo(itemDto.getNo());
            item.setSubject(itemDto.getSubject());
            item.setSubSubject(itemDto.getSubSubject());
            item.setLinkUrl(itemDto.getLinkUrl());
            item.setIndexPage(indexPage); // 연관관계 설정
            indexPage.getItems().add(item);
        });

        return indexPageRepository.save(indexPage).getId();
    }


}