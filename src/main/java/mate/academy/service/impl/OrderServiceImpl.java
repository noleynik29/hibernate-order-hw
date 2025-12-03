package mate.academy.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mate.academy.dao.OrderDao;
import mate.academy.lib.Service;
import mate.academy.model.Order;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.service.OrderService;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order completeOrder(ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setTickets(new ArrayList<>(shoppingCart.getTickets()));
        orderDao.add(order);
        return order;
    }

    @Override
    public List<Order> getOrdersHistory(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Order> query = session.createQuery(
                    "FROM Order o "
                            + "LEFT JOIN FETCH o.tickets "
                            + "WHERE o.user = :user "
                            + "ORDER BY o.orderDate DESC",
                    Order.class
            );
            query.setParameter("user", user);

            return query.getResultList()
                    .stream()
                    .distinct()
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get order history for user " + user, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

