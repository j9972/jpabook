package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED )
public class OrderItem {

    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    private int orderPrice;//주문 가격
    private int count; //주문 수량

    // todo : protected로 기본 생성자를 만들어서 객체 만들어서 set을 데이터 넣는것 막아서 이 방법만 쓰게하기
    // protected OrderItem() {} or @NoArgsConstructor(access = AccessLevel.PROTECTED )
    /*
        이렇게 메소드를 정리하는게 왜 중요하냐면, 생성하는 지점 변경은 이것만 바꾸면 되니까!
        -> 이걸 안쓰면 service 파일에서 객체를 만든 다음에 setXXX() 데이터를 넣는 방식을 쓰게 된다

        권장 방식
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        비권장 방식 [ 매번 메소드 마다 이렇게 적는거임 ]
        OrderItem orderItem = new OrderItem();
        orderItem.setXXX();
        orderItem.setXXX();
        orderItem.setXXX();
     */

    //== 생성 로직===//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        // orderPrice 을 파라미터로 받아가는 이유는 쿠폰 같은 걸로 할인 받게 되어서 수정이 될 수 있기 때문이다.
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //== 비즈니스 로직===//
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
