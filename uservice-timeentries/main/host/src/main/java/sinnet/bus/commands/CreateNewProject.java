package sinnet.bus.commands;

public interface CreateNewProject {
  
  record Command(String name) {
  }
}
