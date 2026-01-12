package com.haero.tonestore.domain.model

/**
 * 톤 세팅에 적용할 수 있는 장르 태그
 */
enum class GenreTag(val displayName: String, val displayNameKo: String) {
    ROCK("Rock", "록"),
    METAL("Metal", "메탈"),
    BLUES("Blues", "블루스"),
    JAZZ("Jazz", "재즈"),
    POP("Pop", "팝"),
    FUNK("Funk", "펑크"),
    COUNTRY("Country", "컨트리"),
    REGGAE("Reggae", "레게"),
    PUNK("Punk", "펑크록"),
    ALTERNATIVE("Alternative", "얼터너티브"),
    INDIE("Indie", "인디"),
    CLASSICAL("Classical", "클래식"),
    ACOUSTIC("Acoustic", "어쿠스틱"),
    WORSHIP("Worship", "워십"),
    OTHER("Other", "기타")
}
