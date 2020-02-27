package info.devlink.microservices.composite.developer.service;

import info.devlink.api.composite.*;
import info.devlink.api.core.contact.Contact;
import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.util.exceptions.NotFoundException;
import info.devlink.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DeveloperCompositeServiceImpl implements DeveloperCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(DeveloperCompositeService.class);

    private final ServiceUtil serviceUtil;
    private DeveloperCompositeIntegration integration;

    @Autowired
    public DeveloperCompositeServiceImpl(ServiceUtil serviceUtil, DeveloperCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    private DeveloperAggregate createDeveloperAggregate(Developer developer, List<Recruiter> recruiters, List<Contact> contacts, String serviceAddress) {

        // 1. Setup developer info
        int developerId = developer.getDeveloperId();
        String firstName = developer.getFirstName();
        String lastName = developer.getLastName();
        String developerType = developer.getDeveloperType();

        // 2. Copy summary recruiter info, if available
        List<RecruiterSummary> recruiterSummaries = (recruiters == null) ? null :
                recruiters.stream()
                        .map(r -> new RecruiterSummary(r.getDeveloperId(), r.getRecruiterId(), r.getRecruiterName(), r.getRecruitingCompany(), r.getCompanyRating()))
                        .collect(Collectors.toList());

        // 3. Copy summary contact info, if available
        List<ContactSummary> contactSummaries = (contacts == null)  ? null :
                contacts.stream()
                        .map(c -> new ContactSummary(c.getContactId(), c.getRecruiterId(), c.getEmail(), c.getPhoneNumber()))
                        .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String developerAddress = developer.getServiceAddress();
        String contactAddress = (contacts != null && contacts.size() > 0) ? contacts.get(0).getServiceAddress() : "";
        String recruiterAddress = (recruiters != null && recruiters.size() > 0) ? recruiters.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, developerAddress, contactAddress, recruiterAddress);

        return new DeveloperAggregate(developerId, firstName, lastName, developerType, recruiterSummaries, contactSummaries, serviceAddresses);
    }

    @Override
    public void createCompositeDeveloper(DeveloperAggregate body) {
        try {

            LOG.debug("createCompositeDeveloper: creates a new composite entity for developerId: {}", body.getDeveloperId());

            Developer developer = new Developer(body.getDeveloperId(), body.getFirstName(), body.getLastName(), body.getDeveloperType(), null);
            integration.createDeveloper(developer);

            if (body.getContacts() != null) {
                body.getContacts().forEach(c -> {
                    Contact contact = new Contact(body.getDeveloperId(), c.getRecruiterId(), c.getContactId(), c.getEmail(), c.getPhoneNumber(), null);
                    integration.createContact(contact);
                });
            }

            if (body.getRecruiters() != null) {
                body.getRecruiters().forEach(r -> {
                    Recruiter recruiter = new Recruiter(body.getDeveloperId(), r.getRecruiterId(),
                            r.getRecruiterName(), r.getRecruiterCompany(), r.getCompanyRating(), null);
                    integration.createRecruiter(recruiter);
                });
            }



            LOG.debug("createCompositeDeveloper: composite entites created for developerId: {}", body.getDeveloperId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeDeveloper failed", re);
            throw re;
        }
    }

    @Override
    public DeveloperAggregate getCompositeDeveloper(int developerId) {
        LOG.debug("getCompositeDeveloper: lookup a developer aggregate for developerId: {}", developerId);

        Developer developer = integration.getDeveloper(developerId);
        if (developer == null) throw new NotFoundException("No developer found for developerId: " + developerId);

        List<Recruiter> recruiters = integration.getRecruiters(developerId);

        List<Contact> contacts = integration.getContacts(developerId);

        LOG.debug("getCompositeDeveloper: aggregate entity found for developerId: {}", developerId);

        return createDeveloperAggregate(developer, recruiters, contacts, serviceUtil.getServiceAddress());
    }

    @Override
    public void deleteCompositeDeveloper(int developerId) {
        LOG.debug("deleteCompositeDeveloper: Deletes a developer aggregate for developerId: {}", developerId);

        integration.deleteDeveloper(developerId);

        integration.deleteRecruiters(developerId);

        integration.deleteContacts(developerId);

        LOG.debug("getCompositeDeveloper: aggregate entities deleted for developerId: {}", developerId);
    }
}
