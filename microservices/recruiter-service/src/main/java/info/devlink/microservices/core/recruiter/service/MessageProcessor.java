package info.devlink.microservices.core.recruiter.service;

import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.api.core.recruiter.RecruiterService;
import info.devlink.api.event.Event;
import info.devlink.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final RecruiterService recruiterService;

    @Autowired
    public MessageProcessor(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }


    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Recruiter> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Recruiter recruiter = event.getData();
                LOG.info("Create recruiter with ID: {}/{}", recruiter.getDeveloperId(), recruiter.getRecruiterId());
                recruiterService.createRecruiter(recruiter);
                break;

            case DELETE:
                int developerId = event.getKey();
                LOG.info("Delete recruiters with DeveloperID: {}", developerId);
                recruiterService.deleteRecruiters(developerId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}