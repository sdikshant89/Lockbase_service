package com.lockbase.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "map_user_seque")
public class MapUserSeQue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_map_user_seque_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_user_id",
            referencedColumnName = "pk_user_id")
    private LoginUser user;

    @ManyToOne
    @JoinColumn(name = "fk_seque_id",
                referencedColumnName = "pk_secque_id")
    private SecurityQuestion securityQuestion;

    @Column(name = "answer")
    private String answer;

    @Column(name = "is_deleted")
    private boolean isDeleted;

//    public boolean isDeleted() {
//        return isDeleted;
//    }
//
//    public void setDeleted(boolean deleted) {
//        isDeleted = deleted;
//    }
}
