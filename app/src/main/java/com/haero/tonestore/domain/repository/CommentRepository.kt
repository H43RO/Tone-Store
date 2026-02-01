package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * 댓글 Repository 인터페이스
 */
interface CommentRepository {

    /**
     * 특정 프리셋의 댓글 목록 조회 (최신순)
     */
    fun getComments(presetId: String): Flow<List<Comment>>

    /**
     * 댓글 작성
     */
    suspend fun addComment(comment: Comment): Result<String>

    /**
     * 댓글 수정
     */
    suspend fun updateComment(comment: Comment): Result<Unit>

    /**
     * 댓글 삭제
     */
    suspend fun deleteComment(commentId: String, presetId: String): Result<Unit>

    /**
     * 특정 프리셋의 댓글 수 조회
     */
    suspend fun getCommentCount(presetId: String): Int
}
