package net.therap.authService.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author erfan
 * @since 7/3/23
 */
@Table
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString
public class Role extends Persistent {
    //todo: ADMIN, EMPLOYEE,

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleSeq")
    @SequenceGenerator(name = "roleSeq", sequenceName = "role_seq", allocationSize = 1)
    private int id;

    @Column(nullable = false)
    private String roleName;
}
