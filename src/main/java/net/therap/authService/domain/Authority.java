package net.therap.authService.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author erfan
 * @since 8/1/23
 */
@Table
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@ToString
public class Authority extends Persistent {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authoritySeq")
    @SequenceGenerator(name = "authoritySeq", sequenceName = "authority_seq", allocationSize = 1)
    private int id;

    @Column(nullable = false)
    private String authorityName;
}
