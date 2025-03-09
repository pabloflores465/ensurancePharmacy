package com.sources.app.entities;

import jakarta.persistence.*;
import com.sources.app.entities.Medicine;

@Entity
@Table(name = "COMMENTS")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMMENTS")
    private Long idComments;

    // Relación ManyToOne con la entidad User
    @ManyToOne
    @JoinColumn(name = "ID_USER", referencedColumnName = "ID_USER")
    private User user;

    // Relación ManyToOne con la misma entidad para el comentario previo (self reference)
    @ManyToOne
    @JoinColumn(name = "ID_PREV_COMMENT", referencedColumnName = "ID_COMMENTS")
    private Comments prevComment;

    @Column(name = "COMMENT_TEXT")
    private String commentText;

    // Relación ManyToOne con la entidad Medicine
    @ManyToOne
    @JoinColumn(name = "ID_MEDICINE", referencedColumnName = "ID_MEDICINE")
    private Medicine medicine;

    // Constructor vacío
    public Comments() {
    }

    // Constructor con campos
    public Comments(User user, Comments prevComment, String commentText) {
        this.user = user;
        this.prevComment = prevComment;
        this.commentText = commentText;
    }

    // Getters y setters
    public Long getIdComments() {
        return idComments;
    }

    public void setIdComments(Long idComments) {
        this.idComments = idComments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comments getPrevComment() {
        return prevComment;
    }

    public void setPrevComment(Comments prevComment) {
        this.prevComment = prevComment;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }
}
