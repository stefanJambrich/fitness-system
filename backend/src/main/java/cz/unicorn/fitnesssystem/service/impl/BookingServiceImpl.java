package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.entity.BookingStatus;
import cz.unicorn.fitnesssystem.entity.CancelReason;
import cz.unicorn.fitnesssystem.repository.BookingRepository;
import cz.unicorn.fitnesssystem.service.BookingService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void cancelUpcomingBookings(UUID membershipId, CancelReason reason, Instant now) {
        bookingRepository.findAllByMembershipIdAndStatusAndTrainingStartsAtAfter(membershipId, BookingStatus.BOOKED, now)
                .forEach(booking -> booking.cancel(now, reason));
    }
}
