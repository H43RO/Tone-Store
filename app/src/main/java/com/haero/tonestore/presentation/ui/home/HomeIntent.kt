package com.haero.tonestore.presentation.ui.home

/**
 * Home 화면의 사용자 인텐트
 */
sealed interface HomeIntent {
    /** 톤 세팅 목록 로드 */
    data object LoadToneSettings : HomeIntent
    
    /** 톤 세팅 선택 (상세 보기) */
    data class SelectToneSetting(val id: String) : HomeIntent
    
    /** 톤 세팅 삭제 */
    data class DeleteToneSetting(val id: String) : HomeIntent
    
    /** 새 톤 세팅 생성 화면으로 이동 */
    data object NavigateToCreate : HomeIntent
    
    /** 네비게이션 이벤트 소비 완료 */
    data object NavigationHandled : HomeIntent
    
    /** 검색창 활성화/비활성화 */
    data class SetSearchActive(val isActive: Boolean) : HomeIntent
    
    /** 검색어 변경 */
    data class UpdateSearchQuery(val query: String) : HomeIntent
}
