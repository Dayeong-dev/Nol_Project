package com.example.nol_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nol_project.dto.NoticeDTO;
import com.example.nol_project.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class adminNoticeController {

    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private HttpSession session;

    @GetMapping("/NoticeList")
    public String NoticeList(
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "category", required = false) String category,
        @RequestParam(name = "keyword", required = false) String keyword,
        RedirectAttributes rttr,
        Model model) {

    	String adminId = (String) session.getAttribute("adminId");
        if (adminId == null) {
        	rttr.addFlashAttribute("message", "관리자 전용 페이지 입니다. 로그인 후 진행해주세요. ");
            return "redirect:/admin/login";
        }

        int pageSize = 10;

        List<NoticeDTO> list = noticeService.getPagedNotices(page, pageSize, category, keyword);
        int totalPages = noticeService.getTotalPages(pageSize, category, keyword);

        model.addAttribute("list", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword);

        model.addAttribute("isAdmin", "admin1234".equals(adminId));

        return "/admin/NoticeList";
    }
    
 // 공지사항 상세보기
    @GetMapping("/NoticeDetail")
    public String NoticeDetail(@RequestParam("nno") int nno, RedirectAttributes rttr, Model model) {
        // 세션에서 사용자 정보 가져오기
        String adminId = (String) session.getAttribute("adminId");
        
        if (adminId == null) {
        	rttr.addFlashAttribute("message", "관리자 전용 페이지 입니다. 로그인 후 진행해주세요. ");
            return "redirect:/admin/login";
        }

        noticeService.increaseHit(nno);

        NoticeDTO notice = noticeService.getNoticeByNno(nno);
        model.addAttribute("notice", notice);

        // 관리자 여부 확인
        boolean isAdmin = "admin1234".equals(adminId);
        model.addAttribute("isAdmin", isAdmin);

        return "/admin/NoticeDetail";
    }

    // 공지사항 등록 폼
    @GetMapping("/NoticeForm")
    public String noticeForm(RedirectAttributes rttr, Model model) {

        String adminId = (String) session.getAttribute("adminId");
        
        if (adminId == null) {
        	//System.out.println("로그인 안됨 → 로그인 페이지로 이동");
        	rttr.addFlashAttribute("message", "관리자 전용 페이지 입니다. 로그인 후 진행해주세요. ");
            return "redirect:/admin/login";
        }
        //System.out.println("로그인 안됨 → 로그인 페이지로 이동");
        return "/admin/NoticeForm";
    }
    
    @PostMapping("/insert")
    public String insert(@ModelAttribute NoticeDTO notice) {
        notice.setAdminId("admin1234");

        if (notice.getIsFixed() != 1) {
            notice.setIsFixed(0);
        }

        noticeService.insert(notice);
        return "redirect:/admin/NoticeList";
    }

    @PostMapping("/NoticeUpdate")
    public String NoticeUpdate(@ModelAttribute NoticeDTO notice) {
        if (notice.getIsFixed() != 1) {
            notice.setIsFixed(0);
        }

        noticeService.NoticeUpdate(notice);
        return "redirect:/admin/NoticeDetail?nno=" + notice.getNno();
    }
    
    // 공지사항 정보 JSON으로 반환 (모달 수정을 위한 API)
    @GetMapping("/getNotice")
    @ResponseBody
    public NoticeDTO getNotice(@RequestParam("nno") int nno) {
        return noticeService.getNoticeByNno(nno);
    }

    // 공지사항 삭제
    @GetMapping("/delete")
    public String delete(@RequestParam("nno") int nno) {
        noticeService.delete(nno);
        return "redirect:/admin/NoticeList";
    }
    
}