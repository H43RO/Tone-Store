package com.haero.tonestore.domain.model

/**
 * 이펙터 페달의 카테고리
 */
enum class PedalCategory {
    /** 드라이브 계열 (Overdrive, Distortion, Fuzz, Boost) */
    DRIVE,

    /** 모듈레이션 계열 (Chorus, Flanger, Phaser, Tremolo) */
    MODULATION,

    /** 시간 기반 (Delay, Reverb) */
    TIME_BASED,

    /** 다이나믹스 (Compressor, Noise Gate) */
    DYNAMICS,

    /** 유틸리티 (Tuner, EQ, Wah) */
    UTILITY,

    /** 피치 (Octave, Whammy) */
    PITCH
}
