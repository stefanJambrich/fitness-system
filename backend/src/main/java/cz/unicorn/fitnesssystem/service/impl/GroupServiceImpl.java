package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.GroupDetails;
import cz.unicorn.fitnesssystem.entity.BookingStatus;
import cz.unicorn.fitnesssystem.entity.CancelReason;
import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import cz.unicorn.fitnesssystem.entity.GroupTrainerRole;
import cz.unicorn.fitnesssystem.entity.Training;
import cz.unicorn.fitnesssystem.entity.TrainingGroup;
import cz.unicorn.fitnesssystem.entity.TrainingStatus;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.helper.RandomCodeHelper;
import cz.unicorn.fitnesssystem.repository.BookingRepository;
import cz.unicorn.fitnesssystem.repository.GroupMembershipRepository;
import cz.unicorn.fitnesssystem.repository.GroupTrainerRepository;
import cz.unicorn.fitnesssystem.repository.TrainingGroupRepository;
import cz.unicorn.fitnesssystem.repository.TrainingRepository;
import cz.unicorn.fitnesssystem.repository.TrainingSeriesRepository;
import cz.unicorn.fitnesssystem.service.GroupAccessService;
import cz.unicorn.fitnesssystem.service.GroupService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private static final int INVITE_CODE_ATTEMPTS = 5;

    private final TrainingGroupRepository groupRepository;
    private final GroupTrainerRepository groupTrainerRepository;
    private final GroupMembershipRepository membershipRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingSeriesRepository seriesRepository;
    private final BookingRepository bookingRepository;
    private final GroupAccessService groupAccessService;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    public List<GroupTrainer> listTrainerGroups(UUID trainerId) {
        return groupTrainerRepository.findAllByIdTrainerIdAndGroupDeletedAtIsNullOrderByGroupNameAsc(trainerId);
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public GroupTrainer createGroup(UUID trainerId, GroupDetails details) {
        AppProperties.Group defaults = properties.group();
        Instant now = clock.instant();
        TrainingGroup group = groupRepository.save(new TrainingGroup(
                normalizeName(details.name()),
                normalizeDescription(details.description()),
                uniqueInviteCode(),
                details.timezone() == null ? defaults.defaultTimezone() : validTimezone(details.timezone()),
                details.defaultCancelHours() == null
                        ? defaults.defaultCancelHours()
                        : details.defaultCancelHours().shortValue(),
                now));
        return groupTrainerRepository.save(new GroupTrainer(group, trainerId, GroupTrainerRole.OWNER, now));
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public GroupTrainer updateGroup(UUID trainerId, UUID groupId, GroupDetails details) {
        GroupTrainer access = groupAccessService.requireTrainer(trainerId, groupId);
        TrainingGroup group = access.getGroup();
        if (details.name() != null) {
            group.setName(normalizeName(details.name()));
        }
        if (details.description() != null) {
            group.setDescription(normalizeDescription(details.description()));
        }
        if (details.timezone() != null) {
            group.setTimezone(validTimezone(details.timezone()));
        }
        if (details.defaultCancelHours() != null) {
            group.setDefaultCancelHours(details.defaultCancelHours().shortValue());
        }
        return access;
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void deleteGroup(UUID trainerId, UUID groupId) {
        GroupTrainer access = groupAccessService.requireOwner(trainerId, groupId);
        Instant now = clock.instant();

        membershipRepository.findAllWithLockByGroupIdOrderById(groupId);
        List<Training> trainings = trainingRepository.findAllWithLockByGroupIdAndStatusAndStartsAtAfterOrderById(
                groupId, TrainingStatus.SCHEDULED, now);
        if (!trainings.isEmpty()) {
            bookingRepository.findAllByTrainingIdInAndStatus(trainings.stream().map(Training::getId).toList(),
                            BookingStatus.BOOKED)
                    .forEach(booking -> booking.cancel(now, CancelReason.TRAINING_CANCELLED));
            trainings.forEach(training -> training.cancel(now));
        }
        seriesRepository.findAllByGroupIdAndEndedAtIsNull(groupId).forEach(series -> series.setEndedAt(now));
        access.getGroup().setDeletedAt(now);
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public GroupTrainer regenerateInviteCode(UUID trainerId, UUID groupId) {
        GroupTrainer access = groupAccessService.requireOwner(trainerId, groupId);
        access.getGroup().regenerateInviteCode(uniqueInviteCode(), clock.instant());
        return access;
    }

    private String uniqueInviteCode() {
        int length = properties.group().inviteCodeLength();
        for (int attempt = 0; attempt < INVITE_CODE_ATTEMPTS; attempt++) {
            String code = RandomCodeHelper.code(length);
            if (!groupRepository.existsByInviteCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("No free invite code after " + INVITE_CODE_ATTEMPTS + " attempts");
    }

    private static String normalizeName(String name) {
        String stripped = name.strip();
        if (stripped.isEmpty()) {
            throw ApiException.validation("name", "must not be blank");
        }
        return stripped;
    }

    private static String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.strip();
    }

    private static String validTimezone(String timezone) {
        if (!ZoneId.getAvailableZoneIds().contains(timezone)) {
            throw ApiException.validation("timezone", "must be an IANA time zone, e.g. Europe/Prague");
        }
        return timezone;
    }
}