package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Models.MembersTemplate;
import aj.FiTracker.FiTrackerExpenses.Repositories.MembersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MembersService {

    private final Logger logger;
    private final MembersRepository membersRepository;

    @Autowired
    public MembersService(MembersRepository membersRepository) {
        this.logger = LoggerFactory.getLogger(MembersService.class);
        this.membersRepository = membersRepository;
    }

    @Transactional
    public void addNewMembers(MembersTemplate membersTemplate) {
        List<User> users = new ArrayList<>();
        logger.info("Started adding new members {} to zoneId {}", membersTemplate.getMembersList(), membersTemplate.getZoneId());
        try {
            for (MembersTemplate.Member member : membersTemplate.getMembersList()) {
                users.add(new User(member.memberId(), membersTemplate.getZoneId()));
            }
            this.membersRepository.saveAll(users);
            logger.error("Added new members {} to zone {} successfull ", users, membersTemplate.getZoneId());

        } catch (Exception e) {
            logger.error("Cannot add members {} to zone {}, exception: {}", membersTemplate.getMembersList(), membersTemplate.getZoneId(), e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public void removeMember(MemberTemplate memberTemplate) {
        logger.info("Started removing new member {} from all zones",memberTemplate.userId());
        try {

            this.membersRepository.deleteByUserId(memberTemplate.userId());
            logger.error("Removes member successfull ",memberTemplate.userId());

        } catch (Exception e) {
            logger.error("Cannot remove member {}, exception: {}", memberTemplate.userId(), e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional
    public void removeMembers(MembersTemplate membersTemplate) {
        List<User> users = new ArrayList<>();
        logger.info("Started removing new members {} to zoneId {}", membersTemplate.getMembersList(), membersTemplate.getZoneId());
        try {
            for (MembersTemplate.Member member : membersTemplate.getMembersList()) {
                this.membersRepository.deleteByUserIdAndZoneId(member.memberId(), membersTemplate.getZoneId());
            }
            logger.error("Removes members {} to zone {} successfull ", users, membersTemplate.getZoneId());

        } catch (Exception e) {
            logger.error("Cannot remove members {} to zone {}, exception: {}", membersTemplate.getMembersList(), membersTemplate.getZoneId(), e.getMessage());
            throw new InternalServerException(e);
        }
    }

    @Transactional(readOnly = true)
    public User getUserByZoneIdAndId(long userId, String zoneId) {
        logger.info("Get user {} for zone {}", userId, zoneId);
        try {
            Optional<User> user = this.membersRepository.findByUserIdAndZoneId(userId, zoneId);
            if (user.isEmpty()) {
                logger.info("Cannot find user {} for zone {}", userId, zoneId);
                throw new UserUnauthorizedException("User does not have privileges to zone");
            }
            logger.info("Return user {} for zone {}", userId, zoneId);
            return user.get();
        } catch (UserUnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Cannot get user {}, zone {}, exception", userId, zoneId, e.getMessage());
            throw new InternalServerException(e);
        }
    }
}
