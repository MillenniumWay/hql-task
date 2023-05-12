package by.itacademy.hibernate.dao;


import by.itacademy.hibernate.entity.Payment;
import by.itacademy.hibernate.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */

    public List<User> findAll(Session session) {

        return session.createQuery("FROM User", User.class).list();
    }



    /**
     * Возвращает всех сотрудников с указанным именем
     */

    public List<User> findAllByFirstName(Session session, String firstName) {
        return session.createQuery("FROM User WHERE personalInfo" +
                ".firstname=:firstname", User.class).setParameter("firstname", firstName).list();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return session.createQuery("FROM User WHERE personalInfo.birthDate=personalInfo.birthDate " +
                "order by personalInfo.birthDate asc", User.class).setMaxResults(limit).list();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return session.createQuery("FROM Company WHERE name=:name", User.class).setParameter("name",companyName).list();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
       return session.createQuery("select pay from Payment pay join pay.receiver us" +
               " where us.company.name = :company_name ORDER BY us.personalInfo.firstname ASC, pay.amount ASC " +
               "", Payment.class).setParameter("company_name", companyName).list();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return session.createQuery("select avg(p.amount) from Payment p where p.receiver.personalInfo.firstname = :firstName and " +
                        "p.receiver.personalInfo.lastname = :lastName", Double.class).setParameter("firstName", firstName)
                .setParameter("lastName", lastName).list().get(0);
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session.createQuery("select c.name, avg(p.amount) from Payment p join p.receiver u " +
                " join u.company c group by c.name order by c.name asc" ).list();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Object[]> isItPossible(Session session) {
        return session.createQuery("select u, avg(p.amount) from Payment p join p.receiver u group by u" +
                        " having avg(p.amount) > (select avg(p.amount) from Payment p) order by u.personalInfo.firstname asc ").list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}