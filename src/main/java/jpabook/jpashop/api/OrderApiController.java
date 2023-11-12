package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    /**
     * V1. 엔티티 직접 노출
     * - 엔티티가 변하면 API 스펙이 변한다.
     * - 트랜잭션 안에서 지연 로딩 필요
     * - 양방향 연관관계 문제 *
     *
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 트랜잭션 안에서 지연 로딩 필요
     *
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경가능) *
     *
     * V4.JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1+NQuery)
     * - 페이징 가능
     *
     * V5.JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1+1Query)
     * - 페이징 가능
     *
     * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
     * - 페이징 불가능... *
     */

    private final OrderRepository orderRepository;


    /**
     * V1. 엔티티 직접 노출 -> 이렇게 하지마~!
     * - Hibernate5Module 모듈 등록, LAZY=null 처리 * - 양방향 관계 문제 발생
     * -> @JsonIgnore ( 이거 안해줘서 무한 루프 걸려서 stackoverflow 에러 난다 )
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersv1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getAddress();  // lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());   // lazy 강제 초기화

        }
        return all;
    }

    /*
        sql문들이 너무 많이 나온다 -> N+1문이 많이 나옴
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * value object -> Address에 city, addres 이런건 노출 괜찮음
     * 엔티티를 노출하지 않아야한다. -> 단순히 껍데기만 엔티티를 노출하지 않고, 속의 내용까지 노츨하지 말아야 한다.
     */
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // 이렇게 속까지 DTO로 변환해서 노출을 막아야한다.
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName; //상품 명
        private int orderPrice; //주문 가격
        private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    /**
     * 1대다를 fetch join 하는 순간 데이터가 뻥튀기 되어서 페이징 쿼리가 아예 안나간다
     * -> toMany 는 fetch join 하면 페이징이 안되고, toOne하면 페이징 처리가 가능하다.
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화 [ 페이징이 많이 해도 잘된다 ]
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }


}




