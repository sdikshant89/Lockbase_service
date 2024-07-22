package com.lockbase.model;

import jakarta.persistence.*;

@Entity
@Table(name = "map_user_seque")
public class MapUserSeQue {

    public MapUserSeQue() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_map_user_seque_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_user_id",
            referencedColumnName = "pk_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "fk_seque_id",
                referencedColumnName = "pk_secque_id")
    private SecurityQuestion securityQuestion;

    @Column(name = "answer")
    private String answer;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "MapUserSeQue{" +
                "id=" + id +
                ", user=" + user +
                ", securityQuestion=" + securityQuestion +
                ", answer='" + answer + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
