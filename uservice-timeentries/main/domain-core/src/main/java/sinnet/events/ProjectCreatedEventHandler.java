package sinnet.events;

import javax.persistence.Entity;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class ProjectCreatedEventHandler {

    
    @EventListener
    void on(ProjectCreated event) {
    }

}

@Entity
class ProjectEntity {
    
}
