package com.example.gias.Object;

import java.io.Serializable;

public class Post implements Serializable {
    private String uidPost;
    private String numberPhonePoster;
    private String title;
    private String description;
    private String feel;
    private String linkImageOne;
    private String linkImageTwo;
    private String linkImageThree;
    private String linkImageFour;
    private String timePost;
    private String numberLike;
    private String numberComment;
    private String numberShare;
    private String linkAvatarPoster;
    private String object;
    private String namePoster;

    public Post(String uidPost, String numberPhonePoster, String title, String description,
                String feel, String linkImageOne, String linkImageTwo,
                String linkImageThree, String linkImageFour, String timePost,
                String numberLike, String numberComment, String numberShare,
                String linkAvatarPoster, String object, String namePoster) {
        this.uidPost = uidPost;
        this.numberPhonePoster = numberPhonePoster;
        this.title = title;
        this.description = description;
        this.feel = feel;
        this.linkImageOne = linkImageOne;
        this.linkImageTwo = linkImageTwo;
        this.linkImageThree = linkImageThree;
        this.linkImageFour = linkImageFour;
        this.timePost = timePost;
        this.numberLike = numberLike;
        this.numberComment = numberComment;
        this.numberShare = numberShare;
        this.linkAvatarPoster = linkAvatarPoster;
        this.object = object;
        this.namePoster = namePoster;
    }

    public Post() {}

    public String getUidPost() {
        return uidPost;
    }

    public void setUidPost(String uidPost) {
        this.uidPost = uidPost;
    }

    public String getNamePoster() {
        return namePoster;
    }

    public void setNamePoster(String namePoster) {
        this.namePoster = namePoster;
    }

    public String getLinkAvatarPoster() {
        return linkAvatarPoster;
    }

    public void setLinkAvatarPoster(String linkAvatarPoster) {
        this.linkAvatarPoster = linkAvatarPoster;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getNumberPhonePoster() {
        return numberPhonePoster;
    }

    public void setNumberPhonePoster(String numberPhonePoster) {
        this.numberPhonePoster = numberPhonePoster;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkImageOne() {
        return linkImageOne;
    }

    public void setLinkImageOne(String linkImageOne) {
        this.linkImageOne = linkImageOne;
    }

    public String getLinkImageTwo() {
        return linkImageTwo;
    }

    public void setLinkImageTwo(String linkImageTwo) {
        this.linkImageTwo = linkImageTwo;
    }

    public String getLinkImageThree() {
        return linkImageThree;
    }

    public void setLinkImageThree(String linkImageThree) {
        this.linkImageThree = linkImageThree;
    }

    public String getLinkImageFour() {
        return linkImageFour;
    }

    public void setLinkImageFour(String linkImageFour) {
        this.linkImageFour = linkImageFour;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getNumberLike() {
        return numberLike;
    }

    public void setNumberLike(String numberLike) {
        this.numberLike = numberLike;
    }

    public String getNumberComment() {
        return numberComment;
    }

    public void setNumberComment(String numberComment) {
        this.numberComment = numberComment;
    }

    public String getNumberShare() {
        return numberShare;
    }

    public void setNumberShare(String numberShare) {
        this.numberShare = numberShare;
    }
}
