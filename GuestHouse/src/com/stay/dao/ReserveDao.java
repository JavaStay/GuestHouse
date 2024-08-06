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
	
	private String location="서울특별시";
	
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
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location= location;
	}
	
	public void create(Customer cust) throws DMLException, DuplicateIDException {
		String query = "INSERT INTO customer(id, name, pass, phone) VALUES(?,?,?,?)";
		
		try(Connection conn = getConnection(); 
			PreparedStatement ps = conn.prepareStatement(query)) {
				
			ps.setString(1, cust.getId());
			ps.setString(2, cust.getName());
			ps.setString(3, cust.getPass());
			ps.setString(4, cust.getPhone());
			
			ps.executeUpdate();
	
			System.out.println("회원 가입에 성공하셨습니다.");
			
		}catch (SQLIntegrityConstraintViolationException e) { 
			throw new DuplicateIDException("[ERROR] 이미 회원가입된 상태입니다. 다시 확인해주세요");
			
		}catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 가입 시 문제가 발생해 가입이 이뤄지지 않았습니다.");
		}
	}
	
	
//	public boolean findCustomer(String id, Connection con) throws SQLException {
//		ResultSet rs = null;
//		String query = "SELECT id FROM customer WHERE id =?";
//		
//		try(PreparedStatement ps = con.prepareStatement(query)) {
//			
//			ps.setString(1, id);
//			rs = ps.executeQuery();
//		} 
//		
//		return rs.next();
//	}
	
	public void login( String id, String pass) throws RecordNotFoundException, DMLException {
		
		String query = "SELECT id, pass FROM customer WHERE id =? and pass =?";
		
		try(Connection conn = getConnection(); 
			PreparedStatement ps = conn.prepareStatement(query)) {
			
			ps.setString(1, id);
			ps.setString(2, pass);
			ResultSet rs = ps.executeQuery();
			
			if( !rs.next()) {
				throw new RecordNotFoundException("[ERROR] 존재하지 않는 id 입니다.");
			}
			else System.out.println("로그인에 성공하셨습니다.");
			
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
			else System.out.println("회원정보 수정에 성공하셨습니다.");
			
		} catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 정보 수정 시 문제가 발생해 수정이 이뤄지지 않았습니다.");
		}
	}
	
	
	public void delete(String id) throws RecordNotFoundException, DMLException  {
		
		String query = "DELETE FROM customer WHERE id =?";
		try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, id);
			
			if(ps.executeUpdate() ==0) {
				throw new RecordNotFoundException("[ERROR] 존재하지 않는 id 입니다.");
			}
			else System.out.println("회원 탈퇴에 성공했습니다.");
			
		} catch (SQLException e) {
			throw new DMLException("[ERROR] 회원 삭제 시 문제가 발생해 삭제가 이뤄지지 않았습니다.");
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
			}else throw new RecordNotFoundException("[ERROR] 게스트 하우스가 존재하지 않습니다.");
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
			throw new DMLException("[ERROR] 검색 도중 문제가 발생했습니다.");
		}
		return list;
	}
	//1. 숙소마다 가격인하율 평균을 내서 숙소 10개 리스트만 출력 
	//2. 방마다 가격인하율 뽑아서 방 30개 리스트 출력 
	public ArrayList<GuestHouse> findByLeadMonth() throws DMLException{
		ArrayList<GuestHouse> list= new ArrayList<>();
		ResultSet rs=null;
		String query="SELECT g1.id,g1.name,g1.address,g1.room_num,g1.room_price,g1.capacity,(g2.room_price - g1.room_price )/g2.room_price*100 인하율 \r\n"
				+ "FROM guesthouse g1 , guesthouse2 g2\r\n"
				+ "WHERE g1.room_num=g2.room_num AND g1.id=g2.id  AND g1.address= ?"
				+ "ORDER BY 인하율 desc;";
		try(
			Connection conn=getConnection();
			PreparedStatement ps = conn.prepareStatement(query);){
			ps.setString(1, location);
			
			rs=ps.executeQuery();
			
			while(rs.next()) {
				list.add(new  GuestHouse(rs.getString(1),rs.getString(1),rs.getString(1),new ArrayList<Room>()));
			}
		}catch (SQLException e) {
			throw new DMLException("[ERROR] 검색 도중 문제가 발생했습니다.");
		}
		
		return list;
	}
	
}//class
