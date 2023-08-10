package com.klaus.saas.system.server.entity;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.commons.model.BaseVO;
import com.klaus.saas.system.model.vo.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = "sys_user", schema = "sample")
public class UserEntity extends BaseEntity {

    @Id
    @Column("id")
    private long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("mobile")
    private String mobile;

    @Column("sex")
    private String sex;

    @Column("status")
    private String status;

    public UserEntity(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.mobile = user.getMobile();
        this.status = user.getStatus();
        this.sex = user.getSex();
    }

    @Override
    protected BaseVO convert() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .name(this.name)
                .password(this.password)
                .email(this.email)
                .mobile(this.mobile)
                .sex(this.sex)
                .status(this.status)
                .build();
    }
}
