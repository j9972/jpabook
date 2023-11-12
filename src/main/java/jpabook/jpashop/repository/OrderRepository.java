package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 검색기능이 있는데, 동적쿼리를 써야한다
    public List<Order> findAllByString(OrderSearch orderSearch) {
        /*
        값이 있다면 이런식으로 쓰면 된다
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status" +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                //.setFirstResult(100) // 페이징 처리 100 페이지부터 ~ 어디까지 페이징 처리
                .setMaxResults(1000) // 최대 천건
                .getResultList();
        */

        //language=JPAQL -> 동적쿼리 처리인데, 실무에서는 이런식으로 안씀
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();

        /*
            queryDSL 로 작성한 코드
            public List<Order> findAll(OrderSearch orderSearch) {

                QOrder order = QOrder.order;
                QMember member = QMember.member;

                return query
                        .select(order)
                        .from(order)
                        .join(order.member, member)
                        .where(statusEq(orderSearch.getOrderStatus()),
                            nameLike(orderSearch.getmemberName()))
                        .limit(1000)
                        .fetch();
            }

            private BooleanExpression statusEq(Orderstatus statusCond) {
                if(statusCond == null){
                    return null;
                }
                return order.status.eq(statusCond);
            }

            private BooleanExpression nameLike(String nameCond) {
                if(!StringUtils.hasText(nameCond)){
                    return null;
                }
                return member.name.like(nameCond);
            }
         */
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .getResultList();
    }

}
