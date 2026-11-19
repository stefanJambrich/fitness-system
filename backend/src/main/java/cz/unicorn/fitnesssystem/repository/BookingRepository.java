package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.dto.BookingCount;
import cz.unicorn.fitnesssystem.entity.Booking;
import cz.unicorn.fitnesssystem.entity.BookingStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByTrainingIdInAndStatus(Collection<UUID> trainingIds, BookingStatus status);

    List<Booking> findAllByMembershipIdAndStatusAndTrainingStartsAtAfter(
            UUID membershipId, BookingStatus status, Instant after);

    // Aggregate per membership, which derived queries can't express.
    @Query("""
            SELECT new cz.unicorn.fitnesssystem.dto.BookingCount(b.membership.id, COUNT(b))
            FROM Booking b
            WHERE b.membership.id IN :membershipIds AND b.status = :status AND b.training.startsAt > :after
            GROUP BY b.membership.id""")
    List<BookingCount> countByMembershipIdInAndStatusAndTrainingStartsAtAfter(
            Collection<UUID> membershipIds, BookingStatus status, Instant after);

    default Map<UUID, Long> countUpcomingByMembershipId(Collection<UUID> membershipIds, Instant now) {
        return countByMembershipIdInAndStatusAndTrainingStartsAtAfter(membershipIds, BookingStatus.BOOKED, now)
                .stream()
                .collect(Collectors.toMap(BookingCount::membershipId, BookingCount::count));
    }
}
