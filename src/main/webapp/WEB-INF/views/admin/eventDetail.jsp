<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>이벤트 상세</title>
	<link rel="stylesheet" href="/css/admin.css">
</head>
<body>
	<jsp:include page="./fragments/aside.jsp"></jsp:include>
	<section id="eventDetail">
		<jsp:include page="./fragments/header.jsp" />
		<div id="eventDetail" class="admin-content">
			<h2>이벤트 상세</h2>
		 	<div class="event-content">
		 		<table>
			    	<tbody>
			      		<tr>
			        		<th>이벤트 명</th>
			        		<td>${event.name}</td>
			      		</tr>
				      	<tr>
				        	<th>이벤트 기간</th>
				        	<td>
				          		<fmt:formatDate value="${event.startDate}" pattern="yyyy/MM/dd" />
				          		-
				          		<fmt:formatDate value="${event.endDate}" pattern="yyyy/MM/dd" />
				        	</td>
				      	</tr>
			      		<tr>
					        <th>이벤트 설명</th>
					        <td>${event.description}</td>
					    </tr>
					    <tr>
					        <td colspan="2">
					          	<img src="${event.contentImgURL}" alt="이벤트 이미지" />
					        </td>
					      </tr>
					    <tr>
					        <td colspan="2" class="event-actions">
					          	<button onclick="updateEvent(${event.eno})">수정</button>
					          	<%-- <button onclick="deleteEvent(${event.eno})">삭제</button> --%>
					        </td>
					    </tr>
					</tbody>
				</table>
				
				<c:choose>
				    <c:when test="${event.cno == null || event.cno == 0}">
				      	<button onclick="regCoupon(${event.eno})">쿠폰 등록하기</button>
				    </c:when>
				    <c:otherwise>
				      	<table class="coupon-table">
				        	<tbody>
				          		<tr>
				            		<th>쿠폰 이름</th>
				            		<td>${event.cname}</td>
				          		</tr>
				          		<tr>
				            		<th>쿠폰 설명</th>
				            		<td>${event.cdescription}</td>
				          		</tr>
				          		<tr>
				            		<th>쿠폰 할인</th>
				            		<td>${event.discount_rate}%</td>
				         		 </tr>
				        	</tbody>
				      	</table>
				    </c:otherwise>
				</c:choose>
		 	</div>
		</div>
	</section>
	<script>
		function updateEvent(eno) {
			location.href = "/admin/updateEvent/" + eno;
		}
		
		function deleteEvent(eno) {
			location.href = "/admin/deleteEvent/" + eno;
		}
				
		function regCoupon(eno) {
			location.href = "/admin/regCoupon/" + eno;
		}
	</script>
</body>
</html>