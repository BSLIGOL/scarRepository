package com.scar.scar.user.domain;

import com.scar.scar.global.entity.BaseEntity;

import com.scar.scar.user.domain.GlobalRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalRole globalRole;

    /**
     * ?????????????????⑤벡瑜????
     */
    public void updateNickName(String newNickName) {
        this.nickName = newNickName;
    }

    /**
     * ?????????????????⑤벡瑜????
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}

