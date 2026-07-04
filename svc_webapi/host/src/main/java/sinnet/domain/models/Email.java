package sinnet.domain.models;

import org.apache.commons.lang3.StringUtils;

public sealed interface Email {

    record Some(String value) implements Email {
    }

    enum None implements Email {
        INSTANCE
    }

    static Email of(String value) {
        if (StringUtils.isEmpty(value)) return None.INSTANCE;
        return new Some(value);
    }
    
}
