package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.entity.CancelReason;
import java.time.Instant;
import java.util.UUID;

public interface BookingService {

    /**
     * Cancels the membership's sign-ups for trainings that haven't started yet, which releases their blocked
     * credits. Sign-ups for trainings that already started stay for the trainer to resolve. The caller must hold
     * the membership lock.
     *
     * @param membershipId membership whose sign-ups to cancel
     * @param reason       why they are cancelled
     * @param now          time of the cancellation
     */
    void cancelUpcomingBookings(UUID membershipId, CancelReason reason, Instant now);
}
