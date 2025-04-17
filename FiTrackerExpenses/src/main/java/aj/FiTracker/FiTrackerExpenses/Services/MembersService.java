package aj.FiTracker.FiTrackerExpenses.Services;

import aj.FiTracker.FiTrackerExpenses.Entities.User;
import aj.FiTracker.FiTrackerExpenses.Exceptions.InternalServerException;
import aj.FiTracker.FiTrackerExpenses.Exceptions.UserUnauthorizedException;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
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
    public void addNewMembers(MemberTemplate memberTemplate) {
        List<User> users = new ArrayList<>();
        logger.info("Started adding new members {} to zoneId {}", memberTemplate.getMembersList(), memberTemplate.getZoneId());
        try {
            for (MemberTemplate.Member member : memberTemplate.getMembersList()) {
                users.add(new User(member.memberId(), memberTemplate.getZoneId()));
            }
            this.membersRepository.saveAll(users);
            logger.error("Added new members {} to zone {} successfull ", users, memberTemplate.getZoneId());

        } catch (Exception e) {
            logger.error("Cannot add members {} to zone {}, exception: {}", memberTemplate.getMembersList(), memberTemplate.getZoneId(), e.getMessage());
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
