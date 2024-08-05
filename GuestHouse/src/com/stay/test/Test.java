package com.stay.test;

import com.stay.dao.ReserveDao;
import com.stay.vo.Customer;

public class Test {

	public static void main(String[] args) {
		ReserveDao dao = ReserveDao.getInstance();
		
		dao.createCustomer(new Customer("10", "신승현", "qwer123", "010-1111-1111"));
		

	}

}
