package com.haero.tonestore.presentation.ui.detail

/**
 * Detail 화면의 사용자 인텐트
 */
sealed interface DetailIntent {
    /** 톤 세팅 로드 */
    data class LoadToneSetting(val id: String) : DetailIntent
    
    /** 편집 화면으로 이동 */
    data object NavigateToEdit : DetailIntent
    
    /** 톤 세팅 삭제 */
    data object DeleteToneSetting : DetailIntent
    
    /** 네비게이션 이벤트 소비 완료 */
    data object NavigationHandled : DetailIntent
}
