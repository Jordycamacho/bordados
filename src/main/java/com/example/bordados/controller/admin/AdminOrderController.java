package com.example.bordados.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.Enums.ShippingStatus;
import com.example.bordados.repository.CustomizedOrderDetailRepository;
import com.example.bordados.repository.OrderCustomRepository;
import com.example.bordados.repository.OrderDetailRepository;
import com.example.bordados.repository.OrderRepository;
import com.example.bordados.service.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Tag(name = "AdminOrderController", description = "Controlador para gestionar las ordenes")
@RequestMapping("/admin/ordenes")
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final OrderCustomRepository orderCustomRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomizedOrderDetailRepository customizedOrderDetailRepository;
    @Autowired
private CategoryService categoryService;

    public AdminOrderController(OrderRepository orderRepository, OrderCustomRepository orderCustomRepository,
            OrderDetailRepository orderDetailRepository, CustomizedOrderDetailRepository customizedOrderDetailRepository) {
        this.orderRepository = orderRepository;
        this.customizedOrderDetailRepository = customizedOrderDetailRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderCustomRepository = orderCustomRepository;
    }


    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }
    @GetMapping("")
    public String showOrders(Model model) {
        // Obtener todas las órdenes normales y personalizadas
        List<Order> orders = orderRepository.findAll();
        List<OrderCustom> customOrders = orderCustomRepository.findAll();

        // Pasar las órdenes a la vista
        model.addAttribute("orders", orders);
        model.addAttribute("customOrders", customOrders);

        return "/admin/order/showOrder";
    }

    @PostMapping("/complete/{id}")
    public String markOrderAsCompleted(@PathVariable Long id, @RequestParam String type,
            RedirectAttributes redirectAttributes) {
        if (type.equals("normal")) {
            Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrada"));
            order.setShippingStatus(ShippingStatus.DELIVERED);
            orderRepository.save(order);
        } else if (type.equals("custom")) {
            OrderCustom orderCustom = orderCustomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Orden personalizada no encontrada"));
            orderCustom.setShippingStatus(ShippingStatus.DELIVERED);
            orderCustomRepository.save(orderCustom);
        }

        redirectAttributes.addFlashAttribute("success", "Orden marcada como completada exitosamente.");
        return "redirect:/admin/ordenes";
    }

    @GetMapping("/detalle/normal/{id}")
    public String showNormalOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Obtener los detalles de la orden (OrderDetail)
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);

        // Pasar los datos a la vista
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);

        return "/admin/order/orderDetail";
    }

    @GetMapping("/detalle/custom/{id}")
    public String showCustomOrderDetails(@PathVariable Long id, Model model) {
        OrderCustom orderCustom = orderCustomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden personalizada no encontrada"));

        // Obtener los detalles de la orden personalizada (CustomizedOrderDetail)
        List<CustomizedOrderDetail> customizedOrderDetails = customizedOrderDetailRepository
                .findByOrderCustom(orderCustom);

        // Pasar los datos a la vista
        model.addAttribute("orderCustom", orderCustom);
        model.addAttribute("customizedOrderDetails", customizedOrderDetails);

        return "/admin/order/customOrderDetail";
    }
}
