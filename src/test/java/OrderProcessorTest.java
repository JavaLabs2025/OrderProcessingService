
import org.example.catalog.InventoryService;
import org.example.catalog.Item;
import org.example.customer.User;
import org.example.order.OrderProcessor;
import org.example.payment.PaymentGateway;
import org.example.pricing.DeliveryCostCalculator;
import org.example.pricing.DiscountCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class) // Включаем магию Mockito!
class OrderProcessorTest {

    @Mock
    private InventoryService inventoryService; // Мок Склада

    @Mock
    private PaymentGateway paymentGateway;     // Мок Банка

    private OrderProcessor orderProcessor;     // Реальный класс, который мы тестируем

    @BeforeEach
    void setUp() {
        // Создаем реальные калькуляторы (нам не нужно их мокать, это чистая логика)
        var fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));
        var discountCalculator = new DiscountCalculator(fixedClock);
        var deliveryCalculator = new DeliveryCostCalculator();

        // Передаем моки и реальные объекты в конструктор процессора
        orderProcessor = new OrderProcessor(
                inventoryService,
                paymentGateway,
                discountCalculator,
                deliveryCalculator
        );
    }

    @Test
    @DisplayName("Должен прервать заказ и НЕ списывать деньги, если товара нет на складе")
    void processOrder_ShouldThrowExceptionAndNotCharge_WhenItemIsOutOfStock() {

        // =========================================================
        // 1. ARRANGE (Подготовка данных и настройка моков)
        // =========================================================

        User user = new User("user-123", false);
        Item laptop = new Item("item-macbook", 2000.0, 1, 2.0, true);
        List<Item> cart = List.of(laptop);

        // УЧИМ МОК: Когда OrderProcessor спросит у склада, есть ли "item-macbook" в количестве 1 шт,
        // склад (мок) должен ответить: НЕТ (false).
        when(inventoryService.inStock("item-macbook", 1)).thenReturn(false);

        // =========================================================
        // 2. ACT (Выполнение действия, которое мы тестируем)
        // =========================================================

        // Так как мы ожидаем ошибку, мы оборачиваем вызов в assertThrows из JUnit 5.
        // Он "поймает" исключение и не даст тесту упасть.
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderProcessor.processOrder(user, cart, "ZONE_1", null);
        });

        // =========================================================
        // 3. ASSERT (Проверка результатов и взаимодействия)
        // =========================================================

        // Проверяем, что текст ошибки правильный
        assertEquals("Item item-macbook is out of stock", exception.getMessage());

        // САМОЕ ГЛАВНОЕ В MOCKITO: Проверяем побочные эффекты!
        // Мы убеждаемся, что процессор остановился на ошибке склада и ДАЖЕ НЕ ПЫТАЛСЯ
        // обратиться к платежному шлюзу. Защищаем клиента от случайного списания денег!
        verifyNoInteractions(paymentGateway);
    }
}