package com.haero.tonestore.data.preset

import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import java.util.UUID

/**
 * 프리셋 이펙터 페달 데이터
 * 일반적인 기타 이펙터 페달을 기본값과 함께 제공
 */
object PresetPedals {

    fun getPresetPedals(): List<Pedal> = listOf(
        createOverdrive(),
        createDistortion(),
        createFuzz(),
        createChorus(),
        createDelay(),
        createReverb(),
        createCompressor(),
        createWah()
    )

    /**
     * 오버드라이브 - 따뜻하고 자연스러운 드라이브
     * 예: Ibanez Tube Screamer, Boss OD-1
     */
    private fun createOverdrive() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Overdrive",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Gain", value = 5f),
            Knob(name = "Tone", value = 5f),
            Knob(name = "Level", value = 5f)
        ),
        order = 0
    )

    /**
     * 디스토션 - 강하고 공격적인 드라이브
     * 예: Boss DS-1, Pro Co RAT
     */
    private fun createDistortion() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Distortion",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Gain", value = 6f),
            Knob(name = "Tone", value = 5f),
            Knob(name = "Level", value = 5f)
        ),
        order = 0
    )

    /**
     * 퍼즈 - 빈티지하고 울림이 강한 드라이브
     * 예: Electro-Harmonix Big Muff, Dallas Arbiter Fuzz Face
     */
    private fun createFuzz() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Fuzz",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Fuzz", value = 7f),
            Knob(name = "Tone", value = 5f),
            Knob(name = "Level", value = 5f)
        ),
        order = 0
    )

    /**
     * 코러스 - 풍성하고 넓은 사운드
     * 예: Boss CE-2, TC Electronic Corona
     */
    private fun createChorus() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Chorus",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Rate", value = 4f),
            Knob(name = "Depth", value = 5f),
            Knob(name = "Level", value = 5f)
        ),
        order = 0
    )

    /**
     * 딜레이 - 에코/반복 효과
     * 예: Boss DD-3, MXR Carbon Copy
     */
    private fun createDelay() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Delay",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Time", value = 5f),
            Knob(name = "Feedback", value = 4f),
            Knob(name = "Mix", value = 4f)
        ),
        order = 0
    )

    /**
     * 리버브 - 공간감/잔향 효과
     * 예: Boss RV-6, Strymon BigSky
     */
    private fun createReverb() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Reverb",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Decay", value = 5f),
            Knob(name = "Tone", value = 5f),
            Knob(name = "Mix", value = 4f)
        ),
        order = 0
    )

    /**
     * 컴프레서 - 다이나믹 제어, 서스테인 증가
     * 예: MXR Dyna Comp, Boss CS-3
     */
    private fun createCompressor() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Compressor",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Sustain", value = 5f),
            Knob(name = "Level", value = 5f),
            Knob(name = "Attack", value = 5f)
        ),
        order = 0
    )

    /**
     * 와우 - 표현력 있는 필터 스윕 효과
     * 예: Dunlop Cry Baby, Vox V847
     */
    private fun createWah() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Wah",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Frequency", value = 5f),
            Knob(name = "Q", value = 5f),
            Knob(name = "Level", value = 5f)
        ),
        order = 0
    )
}
