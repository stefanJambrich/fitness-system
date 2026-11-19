package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "membership_credit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipCredit {

    @Id
    private UUID membershipId;

    private int balance;

    private int blocked;

    private int available;
}