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
        createWah(),
        createPhaser(),
        createFlanger(),
        createTremolo(),
        createOctave(),
        createBoost(),
        createNoiseGate(),
        createTuner(),
        createEQ(),
        createBassPreamp(),
        createWhammy()
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
        order = 0,
        color = 0xFF3EB489
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
        order = 0,
        color = 0xFFFF9800
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
        order = 0,
        color = 0xFF9E9E9E
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
        order = 0,
        color = 0xFF2196F3
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
        order = 0,
        color = 0xFF42A5F5
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
        order = 0,
        color = 0xFF64B5F6
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
        order = 0,
        color = 0xFFE53935
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
        order = 0,
        color = 0xFFB0B0B0
    )

    private fun createPhaser() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Phaser",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Rate", value = 4f),
            Knob(name = "Depth", value = 5f)
        ),
        order = 0,
        color = 0xFFFF5722
    )

    private fun createFlanger() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Flanger",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Rate", value = 4f),
            Knob(name = "Depth", value = 5f),
            Knob(name = "Regen", value = 5f),
            Knob(name = "Manual", value = 5f)
        ),
        order = 0,
        color = 0xFF3F51B5
    )

    private fun createTremolo() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Tremolo",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Rate", value = 4f),
            Knob(name = "Depth", value = 5f),
            Knob(name = "Wave", value = 5f)
        ),
        order = 0,
        color = 0xFFFFEB3B
    )

    private fun createOctave() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Octave",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Dry", value = 5f),
            Knob(name = "Oct1", value = 5f),
            Knob(name = "Oct2", value = 5f)
        ),
        order = 0,
        color = 0xFF1E88E5
    )

    private fun createBoost() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Boost",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Gain", value = 5f)
        ),
        order = 0,
        color = 0xFFFFC107
    )

    private fun createNoiseGate() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Noise Gate",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Threshold", value = 5f),
            Knob(name = "Decay", value = 5f)
        ),
        order = 0,
        color = 0xFF607D8B
    )

    private fun createTuner() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Tuner",
        type = PedalType.PRESET,
        knobs = emptyList(),
        order = 0,
        color = 0xFFFAFAFA
    )

    private fun createEQ() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "EQ",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Band1", value = 5f),
            Knob(name = "Band2", value = 5f),
            Knob(name = "Band3", value = 5f),
            Knob(name = "Band4", value = 5f),
            Knob(name = "Band5", value = 5f)
        ),
        order = 0,
        color = 0xFFCFD8DC
    )

    private fun createBassPreamp() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Bass Preamp",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Blend", value = 5f),
            Knob(name = "Drive", value = 5f),
            Knob(name = "Level", value = 5f),
            Knob(name = "Bass", value = 5f),
            Knob(name = "Treble", value = 5f)
        ),
        order = 0,
        color = 0xFFFFD54F
    )

    private fun createWhammy() = Pedal(
        id = UUID.randomUUID().toString(),
        name = "Whammy",
        type = PedalType.PRESET,
        knobs = listOf(
            Knob(name = "Shift", value = 5f),
            Knob(name = "Mode", value = 5f)
        ),
        order = 0,
        color = 0xFFD32F2F
    )
}
