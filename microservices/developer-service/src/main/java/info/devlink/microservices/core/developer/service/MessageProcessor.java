package info.devlink.microservices.core.developer.service;

import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.developer.DeveloperService;
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

    private final DeveloperService developerService;

    @Autowired
    public MessageProcessor(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Developer> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Developer developer = event.getData();
                LOG.info("Create developer with ID: {}", developer.getDeveloperId());
                developerService.createDeveloper(developer);
                break;

            case DELETE:
                int developerId = event.getKey();
                LOG.info("Delete recruiters with DeveloperID: {}", developerId);
                developerService.deleteDeveloper(developerId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
