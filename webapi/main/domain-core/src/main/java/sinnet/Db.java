package sinnet;

public abstract class Db {
    public abstract static class User {
        /** Max allowed length of User's email. */
        public static final int EMAIL_LENGTH = 50;
    }
    public abstract static class Customer {
        /** Max allowed length of Customer's name. */
        public static final int NAME_LENGTH = 50;
    }

    public abstract static class Project {
        /** Max allowed length of Customer's name. */
        public static final int NAME_LENGTH = 50;
    }
}
