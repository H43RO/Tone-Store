package com.haero.tonestore.domain.model

/**
 * 톤 세팅에 적용할 수 있는 장르 태그
 */
enum class GenreTag(val displayName: String, val displayNameKo: String) {
    ROCK("Rock", "락"),
    HARDROCK("Hard Rock", "하드락"),
    JPOP("J-POP", "J-POP"),
    JROCK("J-ROCK", "J-Rock"),
    METAL("Metal", "메탈"),
    BLUES("Blues", "블루스"),
    JAZZ("Jazz", "재즈"),
    POP("Pop", "팝"),
    PUNK("Punk", "펑크"),
    ALTERNATIVE("Alternative", "얼터네이티브"),
    OTHER("Other", "기타")
}
