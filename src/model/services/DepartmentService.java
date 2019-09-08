package model.services;

import java.util.ArrayList;
import java.util.List;

import entities.model.Department;

public class DepartmentService {
	
	public List<Department> findAll()	{
		List<Department> list = new ArrayList<>();
		list.add(new Department (1, "Book"));
		list.add(new Department (2, "Computer"));
		list.add(new Department (3, "Test"));
		return list;
	}

}
