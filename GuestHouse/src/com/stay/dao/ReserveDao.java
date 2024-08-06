package com.stay.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import com.stay.exception.DMLException;
import com.stay.exception.DuplicateIDException;
import com.stay.exception.RecordNotFoundException;
import com.stay.vo.Customer;
import com.stay.vo.GuestHouse;
import com.stay.vo.Room;

import config.ServerInfo;

public class ReserveDao {
	static String location="서울특별시";
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
	
	public ArrayList<GuestHouse> findByReviceCount() throws DMLException{
		ArrayList<GuestHouse> list= new ArrayList<>();
		ResultSet rs=null;
		String query="SELECT g1.id,g1.name,g1.address,g1.room_num,g1.room_price,g1.capacity,"
				+ "(select count(r.content) from review r join guesthouse g2 "
				+ "where r.GuestHouse_id=g2.id AND r.GuestHouse_id=g1.id) 리뷰수 "
				+ "from guesthouse g1  WHERE  g1.address=? ORDER BY 7 desc;";
		try(
			Connection conn=getConnection();
			PreparedStatement ps = conn.prepareStatement(query)){
			ps.setString(1, location);
			
			rs=ps.executeQuery();
			int i=0;
			String tid=null;
			if(rs.next()) {
				tid=rs.getString(1);
				list.add(new GuestHouse());
				list.get(i).setId(rs.getString(1));
				list.get(i).setName(rs.getString(2));
				list.get(i).setAddress(rs.getString(3));
				list.get(i++).getRooms().add(new Room(rs.getInt(4),rs.getInt(5),rs.getInt(6)));
			}else throw new RecordNotFoundException();
			while(rs.next()) {
				if(!rs.getString(1).equals(tid)) {
					tid=rs.getString(1);
					list.add(new GuestHouse());
					list.get(i).setId(rs.getString(1));
					list.get(i).setName(rs.getString(2));
					list.get(i).setAddress(rs.getString(3));
					
				}else {
					i=i-1;
					
				}list.get(i++).getRooms().add(new Room(rs.getInt(4),rs.getInt(5),rs.getInt(6)));
			}
			
		}catch(SQLException s) {
			throw new DMLException("검색 도중 문제가 발생했습니다.");
		}
		return list;
	}
	
	public ArrayList<GuestHouse> findByLeadMonth(){
		ArrayList<GuestHouse> list= new ArrayList<>();
		ResultSet rs=null;
		String query="";
		
		
		return list;
	}
	
}//class
