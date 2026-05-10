package com.example.ningjingspa.req;

public class ArticleReq {

	private Integer articleId; // 更新時必填
    private String title;
    private String content;
    private String summary;
    private String category;
    private String author;
    private String articleImg;
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getArticleImg() {
		return articleImg;
	}
	public void setArticleImg(String articleImg) {
		this.articleImg = articleImg;
	}
	
	
	
    
    
}
