@Service
@RequiredArgsConstructor
public class IndexPageService {

    private final IndexPageRepository indexPageRepository;

    // 조회: Entity -> ResponseDto 변환
    @Transactional(readOnly = true)
    public IndexPageResponseDto getLatestIndexPage() {
        IndexPage indexPage = indexPageRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("등록된 인덱스 페이지가 없습니다."));

        return IndexPageResponseDto.builder()
                .header(IndexPageResponseDto.HeaderDto.builder()
                        .issueNo(indexPage.getIssueNo())
                        .bgColor(indexPage.getBackgroundColor())
                        .titleTag(indexPage.getTitleTag())
                        .titleTagColor(indexPage.getTitleTagColor())
                        .mainTitle(indexPage.getMainTitle())
                        .mainTitleColor(indexPage.getMainTitleColor())
                        .description(indexPage.getDescription())
                        .descriptionColor(indexPage.getDescriptionColor())
                        .build())
                .config(IndexPageResponseDto.ContentsConfigDto.builder()
                        .tag(indexPage.getContentsTag())
                        .tagColor(indexPage.getContentsTagColor())
                        .noColor(indexPage.getContentsNoColor())
                        .titleColor(indexPage.getContentsTitleColor())
                        .subColor(indexPage.getContentsSubColor())
                        .build())
                .items(indexPage.getItems().stream()
                        .map(item -> IndexPageResponseDto.ItemDto.builder()
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
    public Long saveIndexPage(IndexPageRequestDto dto) {
        IndexPage indexPage = new IndexPage();
        // ... 필드 매핑 로직 (빌더나 Setter 활용)

        // 가변 리스트(IndexItem) 매핑 시 indexPage 참조를 잊지 마세요!
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