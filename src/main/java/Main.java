import authorisation.Authorisation;
import manage.Manage;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Authorisation authorisation = new Authorisation(Authorisation.defRoot, Authorisation.defPw, Authorisation.defUrl);
            try {
                Manage manage = new Manage(authorisation.getJwt(), Manage.defRoot, Manage.defPw, Manage.defUrl);
            } catch (SQLException e) {
                System.out.println("Can't connect to manage base or make an sql operation");
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            System.out.println("Can't connect to user base or make an sql operation");
            throw new RuntimeException(e);
        }
    }
}
