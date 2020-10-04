package sinnet;

import org.springframework.stereotype.Service;

import io.vavr.collection.Stream;

@Service
public class UsersProviderImpl implements UsersProvider {

    // @Autowired
    // private ServicemanRepository mainRepository;

    @Override
    public Stream<UserModel> search() {
        // var elements = mainRepository.findAll().toIterable();
        // return Stream
        //     .ofAll(elements)
        //     .map(it -> new UserModel(Email.of(it.getEmail())));
        return Stream.empty();
    }
}
