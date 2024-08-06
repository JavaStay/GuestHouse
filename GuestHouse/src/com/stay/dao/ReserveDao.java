package com.stay.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.mysql.cj.exceptions.RSAException;
import com.stay.exception.DMLException;
import com.stay.exception.DuplicateIDException;
import com.stay.exception.RecordNotFoundException;
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
	
	
	public void create(Customer cust) throws DMLException, DuplicateIDException {
		String query = "INSERT INTO customer(id, name, pass, phone) VALUES(?,?,?,?)";
		
		try(Connection conn = getConnection(); 
			PreparedStatement ps = conn.prepareStatement(query)) {
				
			ps.setString(1, cust.getId());
			ps.setString(2, cust.getName());
			ps.setString(3, cust.getPass());
			ps.setString(4, cust.getPhone());
	
			System.out.println(ps.executeUpdate() + " Customer 등록 성공");
			
		}catch (SQLIntegrityConstraintViolationException e) { 
			throw new DuplicateIDException("[ERROR] 이미 회원가입된 상태입니다. 다시 확인해주세요");
			
		}catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 가입 시 문제가 발생해 가입이 이뤄지지 않았습니다.");
		}
	}
	
	
	public boolean findCustomer(String id, Connection con) throws SQLException {
		ResultSet rs = null;
		String query = "SELECT id FROM customer WHERE id =?";
		
		try(PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, id);
			rs = ps.executeQuery();
		} 
		
		return rs.next();
	}
	
	public void login( String id, String pass) throws RecordNotFoundException, DMLException {
		
		String query = "SELECT id, pass FROM customer WHERE id =? and pass =?";
		
		try(Connection conn = getConnection(); 
			PreparedStatement ps = conn.prepareStatement(query)) {
			
			ps.setString(1, id);
			ps.setString(2, pass);
			ResultSet rs = ps.executeQuery();
			
			if( rs.next()) {
				throw new RecordNotFoundException("[ERROR] 존재하지 않는 id 입니다.");
			}
			
		} catch (SQLException e) {
			throw new DMLException("[ERROR] 로그인 시 문제가 발생해 로그인이 이뤄지지 않았습니다.");
		}
		
		
	}
	
	
	public void update(Customer cust) throws RecordNotFoundException, DMLException {
		
		String query = "UPDATE customer SET pass = ?, phone = ? where id = ?";
		try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, cust.getPass());
			ps.setString(2, cust.getPhone());
			ps.setString(3, cust.getId());
			
			if(ps.executeUpdate() == 0) {
				throw new RecordNotFoundException("[ERROR] 존재하지 않는 id 입니다.");
			}
			
		} catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 수정 시 문제가 발생해 수정이 이뤄지지 않았습니다.");
		}
	}
	
	
	public void delete(Customer cust) throws RecordNotFoundException, DMLException  {
		
		String query = "DELETE FROM customer WHERE id =?";
		try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, cust.getId());
			
			if(ps.executeUpdate() ==0) {
				throw new RecordNotFoundException("[ERROR] 존재하지 않는 id 입니다.");
			}
			
		} catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 삭제 시 문제가 발생해 삭제가 이뤄지지 않았습니다.");
		}
	}
	
	
	

}
