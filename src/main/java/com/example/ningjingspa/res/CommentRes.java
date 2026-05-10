package com.example.ningjingspa.res;

import java.time.LocalDateTime;

public class CommentRes {

	private Integer commentId;
    private Integer articleId;
    private Integer userId;
    private String userName;
    private String content;
    private LocalDateTime commentAt;
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getCommentAt() {
		return commentAt;
	}
	public void setCommentAt(LocalDateTime commentAt) {
		this.commentAt = commentAt;
	}
    
    
}
