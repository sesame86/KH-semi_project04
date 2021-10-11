package admin.product.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import admin.product.model.service.adminProductService;
import product_img.vo.ProductImg;
import product_option.vo.ProductOption;
import product_post.vo.ProductPost;
import user.vo.User;

/**
 * Servlet implementation class InsertProductServlet
 */
@WebServlet("/insertproduct")
public class InsertProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertProductServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		User memberLoginInfo = (User)request.getSession().getAttribute("memberLoginInfo");
		String id = null;
		if(memberLoginInfo != null) {
			id = memberLoginInfo.getUid();
		}
		//잠깐 쓰자
		if(id == null) {
			id = "admin";
		}
		//사진 업로드 설정
		// 파일 저장 경로 (web 경로 밑에 해당 폴더를 생성해 주어야 한다)
		String fileSavePath = "upload";
		// 파일 크기 10M 제한
		int uploadSizeLimit = 10 * 1024 * 1024;
		String encType = "UTF-8";
		
		if(!ServletFileUpload.isMultipartContent(request))
			response.sendRedirect("view/error/Error.jsp");
		
		ServletContext context = getServletContext();
		String uploadPath = context.getRealPath(fileSavePath);
		MultipartRequest multi = new MultipartRequest(request, // request 객체
			uploadPath, // 서버 상 업로드 될 디렉토리
			uploadSizeLimit, // 업로드 파일 크기 제한
			encType, // 인코딩 방법
			new DefaultFileRenamePolicy() // 동일 이름 존재 시 새로운 이름 부여 방식
		);
		
		//상품
		String uploadTitleImg = "upload/" + multi.getFilesystemName("uploadTitleImg");
		String productTitle = multi.getParameter("productTitle");
		String productIntro=multi.getParameter("productIntro");
		String cateListStr=multi.getParameter("cateList");
		int cateList=0;
		if(cateListStr != null) {
			cateList=Integer.parseInt(cateListStr);
		}
		String feeTextStr=multi.getParameter("feeText");
		int feeText=0;
		if(feeTextStr != null) {
			feeText= Integer.parseInt(feeTextStr);
		}
		int productStock=100;
		String productOptionPriceStr=multi.getParameter("productOptionPrice");
		
		int productOptionPrice=0;
		if(productOptionPriceStr != null) {
			productOptionPrice=Integer.parseInt(productOptionPriceStr);
		}
		
		ProductPost productVo = new ProductPost(cateList, productTitle, uploadTitleImg,  productOptionPrice,  feeText, productStock);
		
		
		//상품 옵션
		ArrayList<ProductOption> ProductOption = new ArrayList<ProductOption>();
		String optionName = null;
		String optionUnit = null;
		//proOptNameId
		String optionCountStr=multi.getParameter("optionCount");
		int optionCount =0;
		if(optionCountStr != null) {
			optionCount= Integer.parseInt(optionCountStr);
		}
		for(int i=0; i<=optionCount; i++) {
			ProductOption optionVo = new ProductOption(optionName);
			optionVo.setPro_option_content(multi.getParameter("productOptionName_"+i));
			ProductOption.add(optionVo);
		}
		
		ArrayList<ProductImg> ProductImg = new ArrayList<ProductImg>();
		String stepImg=null;
		
		String stepCount = multi.getParameter("stepCount");
		int stepCnt = 0;
		if(stepCount != null) {
			stepCnt = Integer.parseInt(stepCount);
		}
		for(int i=0;i<=stepCnt;i++) {
			ProductImg imgVo = new ProductImg(stepImg);
			imgVo.setPro_content_img("upload/" +multi.getFilesystemName("uploadStepImg_"+ i));
			ProductImg.add(imgVo);
		}
		int result = new adminProductService().insertProduct(productVo, ProductOption, ProductImg);
		
		if(result > 0) {
			request.setAttribute("msg", "레시피 게시글 작성을 성공했습니다.");
			request.getRequestDispatcher("main").forward(request, response);
		}else {
			request.setAttribute("msg", "레시피 게시글 작성을 실패했습니다.");
			request.getRequestDispatcher("main").forward(request, response);
		}
		
		
		
		request.getRequestDispatcher("./WEB-INF/view/productInsert.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
