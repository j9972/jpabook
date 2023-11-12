package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/** *
 * xToOne(ManyToOne, OneToOne) 관계 최적화 * Order ( 성능 최적화 )
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;//의존관계 주 입

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리 * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }

        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X) *
     * - 단점: 지연로딩으로 쿼리 N번 호출
     * // List로 반환하면 안되고 Result로 감싸야 한다
     * List<SimpleOrderDto> ordersV2() { -> Result<SimpleOrderDto> ordersV2() {
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        // DTO에서 엔티티를 파라미터로 받는 것은 괜찮다
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - fetch join으로 쿼리 1번 호출
     * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     *
     * v2랑 v3는 같지만, 쿼리수가 다르다 ( v2는 N+1문제로 5번 나오는데, v3는 fetch join으로 쿼리가 1번 나옴 )
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
        /*
        이렇게도 할 수 있다.
        return orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
         */
    }

    /**
     * V4. JPA에서 DTO로 변환해서 조회하는게 아니라 바로 DTO 조회
     * -쿼리1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     *
     * v3랑 같지만, v3는 데이터를 더 많이 퍼올라고, v4는 내가 query문을 작성해서 원하는것만 가져온다
     * v3, v4은 우열을 가릴 수 없다. -> 트레이드 오프다
     * 왜내하면, v3은 재사용성이 좋고 성능은 조금 낮지만, v4는 재사용성이 좋지 못하지만, 성능이 좀 더 좋다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

}
