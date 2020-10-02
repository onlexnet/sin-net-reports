package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.Stream;
import sinnet.user.ServicemanRepository;

@Service
public class UsersProviderImpl implements UsersProvider {

    @Autowired
    private ServicemanRepository mainRepository;

    @Override
    public Stream<UserModel> search() {
        return Stream
            .ofAll(mainRepository.findAll())
            .map(it -> new UserModel(it.getEmail()));
    }
}
