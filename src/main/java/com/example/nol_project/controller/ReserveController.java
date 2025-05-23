package com.example.nol_project.controller;

import com.example.nol_project.dto.TicketDTO;
import com.example.nol_project.dto.ReserveDTO;
import com.example.nol_project.service.TicketService;

import jakarta.servlet.http.HttpSession;

import com.example.nol_project.service.MyPageService;
import com.example.nol_project.service.ReserveService;

import org.springframework.beans.factory.annotation.Autowired;
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

    // GET 예매 메인 페이지 - 티켓 목록 출력
    @GetMapping("/reserve")
    public String showReservePage(Model model) {
        List<TicketDTO> ticketList = ticketService.getAllTickets();
        System.out.println("🎫 티켓 개수: " + ticketList.size());
        model.addAttribute("ticketList", ticketList);
        return "reserve"; // reserve.jsp
    }

    // ✅ [GET] 티켓 선택 후 날짜/수량 입력 폼
    @GetMapping("/reserveForm")
    public String showReserveForm(@RequestParam("tno") int tno, Model model) {
        TicketDTO ticket = ticketService.getTicketByTno(tno);
        model.addAttribute("ticket", ticket);
        return "reserveForm"; // reserveForm.jsp
    }

    // POST 예매 처리
    @PostMapping("/reserve")
    public String processReserve(@ModelAttribute ReserveDTO dto, RedirectAttributes rttr) {
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
    public String showMyPage(HttpSession session, Model model) {
        String id = (String)session.getAttribute("id"); // 로그인 연동 전 하드코딩
        List<Map<String, Object>> list = mypageService.getMyReserveList(id);
        for (Map<String, Object> row : list) {
            System.out.println("🔍 Row keys: " + row.keySet());
            System.out.println("🔍 Row values: " + row.values());
        }
        model.addAttribute("myReserveList", list);
        return "mypage"; 
    }
}
