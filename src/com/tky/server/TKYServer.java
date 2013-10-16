package com.tky.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.json.JSONException;
import org.json.JSONObject;

public class TKYServer extends HttpServlet {

	private static final long serialVersionUID = 4988624175878648807L;
	private static Connection conn;
	private static Statement st;
	private ServletFileUpload upload;
	private final long MAXSize = 4194304*2L;//4*2MB
	private String filedir=null;
    
	@Override
	public void init(ServletConfig config) throws ServletException {               //��ʼ��
		super.init();
		
		FileItemFactory factory = new DiskFileItemFactory();// Create a factory for disk-based file items
		this.upload = new ServletFileUpload(factory);// Create a new file upload handler
		this.upload.setSizeMax(this.MAXSize);// Set overall request size constraint 4194304
		filedir=config.getServletContext().getRealPath("images");
		System.out.println("filedir="+filedir);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String s = req.getParameter("json");// �õ��ͻ��˷��͵�����
		System.out.println("doPost();"+s);
		if(s==null){
			System.out.println("1");
		}
		else{
			PrintWriter out = null;
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(s);
				String flag = jsonObject.getString(JSONKey.FLAG);

				System.out.println("doPost();s is null and flag=" + flag);
				switch (flag) {
					case JSONKey.FLAG_WELCOM:
						jsonObject.put(JSONKey.WELCOM_LOG, "�����ɹ�");
						break;
					case JSONKey.FLAG_CHI:
						//��ȡ������dbo.chi�������ݱ������
						jsonObject = searchForChi(jsonObject);
						System.out.println("chi");
						break;
					case JSONKey.FLAG_LOGIN:
						System.out.println("flag_login");
						break;
					case JSONKey.FLAG_HE:
						System.out.println("he");
						break;
					case JSONKey.FLAG_ORDER:
						CreatOrder creat = new CreatOrder();
						jsonObject.put(JSONKey.FLAG_ORDER, creat.creatOrderC());
						break;
					default:
						System.out.println("�����з��С�����"+flag);
						break;
				}
				resp.setContentType("text/plain");
				resp.setCharacterEncoding("UTF-8");
				out = resp.getWriter();
				System.out.println("result =" + jsonObject.toString());
				out.write(jsonObject.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (out != null)
				out.close();
		}
	}
	public static JSONObject searchForChi(JSONObject jsonObject) throws JSONException{
		conn = DBHelper.getConnection();
		try {
			String sql = "select * from chi";
			System.out.println("==============1========>");
			// ��������ִ�о�̬sql����Statement����
			st = (Statement) conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			String imgClass;
			String imgUrl;
			String title;
			float money;
			int isHave;
			int imgCount;
			String imgId;
			int num = 0;
			while (rs.next()){
				num = num + 1;
				System.out.println("=========================================================================>");
				imgId = rs.getString("imgid");
				imgClass = rs.getString("imgclass");
				imgUrl = rs.getString("imgurl");
				money = rs.getFloat("imgprice");
				imgCount = rs.getInt("imgcount");
				isHave = rs.getInt("ishave");
				title = rs.getString("imgtitle");
				try {
					jsonObject.put(JSONKey.jsonKey_class+num, imgClass);
					jsonObject.put(JSONKey.jsonKey_id+num, imgId);
					jsonObject.put(JSONKey.jsonKey_count+num, imgCount);
					jsonObject.put(JSONKey.jsonKey_ishave+num, isHave);
					jsonObject.put(JSONKey.jsonKey_URL+num, imgUrl);
					jsonObject.put(JSONKey.jsonKey_title+num, title);
					jsonObject.put(JSONKey.jsonKey_price+num, money);
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
			jsonObject.put(JSONKey.TotalNum, num);
			System.out.println("chi:"+jsonObject.toString());
			// �ر����ݿ�����
			conn.close();
			return jsonObject;
		} catch (SQLException e) {
			System.out.println("��������ʧ��" + e.getMessage());
		}
		return null;
	}
	public static JSONObject searchForSpinner(JSONObject jsonObject){
		conn = DBHelper.getConnection();
		try {
			String sql = "select * from photo200 WHERE datatime = ( SELECT max([datatime]) FROM photo200)";
			// ��������ִ�о�̬sql����Statement����
			st = (Statement) conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			String id = null;
			int num;
			int classNum;
			String msg = null;
			if(rs.next()) {
				 id = rs.getString("id");
				 //����ָ��id����Ϣ
				 sql = "select * from photo200 where id = '" + id + "'";
				 ResultSet rs2 = st.executeQuery(sql);
				 //����Ϣ���ݵ�json��䵱��
				 while (rs2.next()){
					 System.out.println("=========================================================================>");
					 id = rs2.getString("id");
					 num = rs2.getInt("num");
					 classNum = rs2.getInt("class");
					 msg = rs2.getString("message");
					 try {
						 jsonObject.put("num", num);
						 jsonObject.put(""+classNum, msg);
					 } catch (JSONException e) {
						e.printStackTrace();
					 }
					 System.out.println("===>  id="+id+"  num="+num+"   classNum="+classNum +"    msg = "+msg);
				 }
			}
			System.out.println("sql==1===?"+jsonObject);
			// �ر����ݿ�����
			conn.close();
			return jsonObject;
		} catch (SQLException e) {
			System.out.println("��������ʧ��" + e.getMessage());
		}
		return null;
	}
}
