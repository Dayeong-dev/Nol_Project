package com.example.nol_project.controller;

import com.example.nol_project.dto.TicketDTO;
import com.example.nol_project.dto.UserCouponDTO;
import com.example.nol_project.dao.CouponDAO;
import com.example.nol_project.dao.ReserveDAO;
import com.example.nol_project.dao.UserCouponDAO;
import com.example.nol_project.dto.ReserveDTO;
import com.example.nol_project.service.TicketService;

import jakarta.servlet.http.HttpSession;

import com.example.nol_project.service.CouponService;
import com.example.nol_project.service.MyPageService;
import com.example.nol_project.service.ReserveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ReserveController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ReserveService reserveService;
    
    @Autowired
    private MyPageService mypageService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private UserCouponDAO userCouponDAO;
    
    @Autowired
    private ReserveDAO reserveDao;

    // GET 예매 메인 페이지 - 티켓 목록 출력
    @GetMapping("/reserve")
    public String showReservePage(Model model, HttpSession session, RedirectAttributes rttr) {
    	 String id = (String) session.getAttribute("id");

         if (id == null || id.trim().isEmpty()) {
             rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
             return "redirect:/login"; // 로그인 페이지로 이동
         }
        List<TicketDTO> ticketList = ticketService.getAllTickets();
        System.out.println("🎫 티켓 개수: " + ticketList.size());
        model.addAttribute("ticketList", ticketList);
        return "reserve"; // reserve.jsp
    }

    // 티켓 선택 후 날짜/수량 입력 폼
    @GetMapping("/reserveForm")
    public String showReserveForm(@RequestParam("tno") int tno, HttpSession session, Model model, RedirectAttributes rttr) {
        TicketDTO ticket = ticketService.getTicketByTno(tno);
        model.addAttribute("ticket", ticket);

        String id = (String) session.getAttribute("id");
        if (id == null || id.trim().isEmpty()) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/login"; // 로그인 페이지로 이동
        }
        System.out.println("✅ 현재 로그인 ID: " + id);

        List<UserCouponDTO> userCoupons = couponService.getCouponsByUserId(id);
        System.out.println("✅ 쿠폰 개수 (컨트롤러 내부): " + userCoupons.size());

        for (UserCouponDTO c : userCoupons) {
            System.out.println("👉 쿠폰: " + c.getName() + ", 할인율: " + c.getDiscount_rate());
        }

        model.addAttribute("userCoupons", userCoupons); // ✅ 이 줄이 있어야 JSP에 전달됨

        return "reserveForm";
    }


    // POST 예매 처리
    @PostMapping("/reserve")
    public String processReserve(@ModelAttribute ReserveDTO dto, RedirectAttributes rttr, HttpSession session) {
        String id = (String) session.getAttribute("id");

         if (id == null || id.trim().isEmpty()) {
             rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
             return "redirect:/login"; // 로그인 페이지로 이동
         }
         
        int unitPrice = ticketService.getTicketByTno(dto.getTno()).getPrice();
        int quantity = dto.getQuantity();
        int discount = dto.getDiscountRate();

        int total = unitPrice * quantity;
        if (discount > 0) {
            total -= total * discount / 100;
        }
        if (dto.getUcno() != 0) {
            userCouponDAO.updateCouponUsed(dto.getUcno());
        }

        dto.setTotalPrice(total); // 실제 결제 금액 저장
     
        boolean result = reserveService.processReserve(dto);
        if (result) {
            rttr.addFlashAttribute("msg", "예매 성공!");
            return "redirect:/mypage";
        } else {
            rttr.addFlashAttribute("msg", "예매 실패: 수량 부족");
            return "redirect:/reserveForm?tno=" + dto.getTno();
        }
    }
    
    @GetMapping("/mypage")
    public String showMyPage(HttpSession session, Model model, RedirectAttributes rttr) {
        String id = (String) session.getAttribute("id");

        if (id == null || id.trim().isEmpty()) {
            rttr.addFlashAttribute("msg", "로그인 후 이용해주세요.");
            return "redirect:/login"; // 로그인 페이지로 이동
        }

        List<Map<String, Object>> list = mypageService.getMyReserveList(id);

        for (Map<String, Object> row : list) {
            System.out.println("🔍 Row keys: " + row.keySet());
            System.out.println("🔍 Row values: " + row.values());
        }

        model.addAttribute("myReserveList", list);
        return "mypage";
    }
    
    @PostMapping("/deleteReservation")
    public String deleteReservation(@RequestParam("rno") int rno,
                                    @RequestParam("id") String id,
                                    RedirectAttributes rttr) {
        try {
            int result = reserveDao.deleteReservation(rno, id);

            if (result > 0) {
                rttr.addFlashAttribute("msg", "예매가 삭제되었습니다.");
            } else {
                rttr.addFlashAttribute("msg", "예매 삭제에 실패했습니다.");
            }

        } catch (DataIntegrityViolationException e) {
            rttr.addFlashAttribute("msg", "❗ 리뷰가 작성된 예매는 삭제할 수 없습니다.");
        }

        return "redirect:/mypage";
    }
}    