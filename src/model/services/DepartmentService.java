package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll() {
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Books"));
		list.add(new Department(2, "Books"));
		list.add(new Department(3, "Books"));
		list.add(new Department(4, "Books"));
		list.add(new Department(5, "Books"));
		return list;
	}
}