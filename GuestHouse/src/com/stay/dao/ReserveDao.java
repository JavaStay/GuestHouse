package com.stay.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.stay.exception.DMLException;
import com.stay.exception.DuplicateIDException;
import com.stay.vo.Customer;

import config.ServerInfo;

public class ReserveDao {
	
	//싱글톤
	private static ReserveDao dao = new ReserveDao();

	private ReserveDao() {
		//driver 로딩
		try {
			Class.forName(ServerInfo.DRIVER_NAME);
			System.out.println("Driver Loading 성공");
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Loading 실패");
		}
		
	};
	
	public static ReserveDao getInstance() {
		return dao;
	}
	
	public Connection getConnection() throws SQLException{
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASSWORD);
		System.out.println("DB Connection 성공");
		return conn;
	}	
	
	public void createCustomer(Customer cust) throws DMLException, DuplicateIDException {
		String query = "INSERT INTO customer(id, name, pass, phone) VALUES(?,?,?,?)";
		
		try(Connection conn = getConnection(); 
			PreparedStatement ps = conn.prepareStatement(query)) {
				
			ps.setString(1, cust.getId());
			ps.setString(2, cust.getName());
			ps.setString(3, cust.getPass());
			ps.setString(4, cust.getPhone());
	
			System.out.println(ps.executeUpdate() + " Customer 등록 성공");
			
		}catch (SQLIntegrityConstraintViolationException e) { 
			throw new DuplicateIDException("이미 회원가입된 상태입니다. 다시 확인해주세요");
			
		}catch (SQLException e) {	//sql 문법 오류
			throw new DMLException("회원 가입 시 문제가 발생해 가입이 이뤄지지 않았습니다.");
		}
	}
	
	
	

}
