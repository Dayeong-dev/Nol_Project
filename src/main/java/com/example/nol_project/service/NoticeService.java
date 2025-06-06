package com.example.nol_project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nol_project.dao.INoticeDAO;
import com.example.nol_project.dto.NoticeDTO;

@Service
@Transactional
public class NoticeService {

    @Autowired
    private INoticeDAO noticeDAO;

    public List<NoticeDTO> getAllNotices() {
        return noticeDAO.selectAll();
    }

    public NoticeDTO getNoticeByNno(int nno) {
        return noticeDAO.selectByNno(nno);
    }

    public void insert(NoticeDTO notice) {
        noticeDAO.insert(notice);
    }

    public void NoticeUpdate(NoticeDTO notice) {
        noticeDAO.noticeUpdate(notice);
    }

    public void delete(int nno) {
        noticeDAO.delete(nno);
    }
    
    public void increaseHit(int nno) {
        noticeDAO.increaseHit(nno);
    }
    
//    public List<NoticeDTO> getPagedNotices(int page, int pageSize) {
//        int start = (page - 1) * pageSize + 1;
//        int end = page * pageSize;
//        return noticeDAO.selectPaged(start, end);
//    }

//    public int getTotalPages(int pageSize) {
//        int totalNotices = noticeDAO.countNotices();
//        return (int) Math.ceil((double) totalNotices / pageSize);
//    }
    
    public List<NoticeDTO> getPagedNotices(int page, int pageSize, String category, String keyword) {
    	int start = (page - 1) * pageSize + 1;
    	int end = page * pageSize;
    	if (category == null || category.isBlank()) category = "";
    	if (keyword == null || keyword.isBlank()) keyword = "";
    	return noticeDAO.selectPagedFiltered(start, end, category, keyword);
    }

    public int getTotalPages(int pageSize, String category, String keyword) {
    	int total = noticeDAO.countFiltered(category, keyword);
    	if (category == null || category.isBlank()) category = "";
    	if (keyword == null || keyword.isBlank()) keyword = "";
    	return (int) Math.ceil((double) total / pageSize);
    }
}