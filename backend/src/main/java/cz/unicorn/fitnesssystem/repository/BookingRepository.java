package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.Booking;
import cz.unicorn.fitnesssystem.entity.BookingStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByTrainingIdInAndStatus(Collection<UUID> trainingIds, BookingStatus status);
}