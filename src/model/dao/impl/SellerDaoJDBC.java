package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.dao.SellerDao;
import db.DB;
import db.DbException;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement query = null;
		try {
			query = conn.prepareStatement(
					"INSERT INTO seller " +
					"(name, email, birthDate, baseSalary, departmentId) " +
					"VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
					);
			
			query.setString(1, obj.getName());
			query.setString(2, obj.getEmail());
			query.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			query.setDouble(4, obj.getBaseSalary());
			query.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = query.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet result = query.getGeneratedKeys();
				if (result.next()) {
					int id = result.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(result);
			} else {
				throw new DbException("Unexpect error! No rows affected!");
			}
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(query);
		}
		
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement query = null;
		try {
			query = conn.prepareStatement(
					"UPDATE seller " +
					"SET name=?, email=?, birthDate=?, baseSalary=?, departmentId=? " +
					"WHERE id=?"
					);
			
			query.setString(1, obj.getName());
			query.setString(2, obj.getEmail());
			query.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			query.setDouble(4, obj.getBaseSalary());
			query.setInt(5, obj.getDepartment().getId());
			query.setInt(6, obj.getId());
			
			query.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(query);
			
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement query = null;
		try {
			query = conn.prepareStatement(
					"DELETE FROM seller WHERE id=?"
					);
			query.setInt(1, id);
			
			query.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(query);
			
		}
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement query = null;
		ResultSet result = null;
		
		try {
			query = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + 
					"FROM seller INNER JOIN department " + 
					"ON seller.DepartmentId = department.Id " + 
					"WHERE seller.Id = ?;"
					);
			
			query.setInt(1, id);
			result = query.executeQuery();
			
			if (result.next()) {
				var dep = instantiateDepartment(result);
				
				var seller = instatianteSeller(result, dep);
				
				return seller;
				
			}
			return null;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(query);
			
		}
	}

	private Seller instatianteSeller(ResultSet result, Department dep) throws SQLException {
		var seller = new Seller();
		seller.setId(result.getInt("id"));
		seller.setName(result.getString("name"));
		seller.setEmail(result.getString("email"));
		seller.setBaseSalary(result.getDouble("baseSalary"));
		seller.setBirthDate(new java.util.Date(result.getTimestamp("birthDate").getTime()));
		seller.setDepartment(dep);
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet result) throws SQLException {
		var dep = new Department();
		dep.setId(result.getInt("departmentId"));
		dep.setName(result.getString("depName"));
		
		return dep;
	}
	

	@Override
	public List<Seller> findAll() {
		PreparedStatement query = null;
		ResultSet result = null;
		
		try {
			query = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + 
					"FROM seller INNER JOIN department " + 
					"ON seller.DepartmentId = department.Id " + 
					"ORDER BY Name"
					);
			
			result = query.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (result.next()) {
				
				var dep = map.get(result.getInt("departmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(result);
					map.put(result.getInt("departmentid"), dep);
				}
				
				
				var seller = instatianteSeller(result, dep);
				
				sellers.add(seller);
				
			}
			return sellers;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(query);
			
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement query = null;
		ResultSet result = null;
		
		try {
			query = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + 
					"FROM seller INNER JOIN department " + 
					"ON seller.DepartmentId = department.Id " + 
					"WHERE DepartmentId = ? " + 
					"ORDER BY Name"
					);
			
			query.setInt(1, department.getId());
			result = query.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (result.next()) {
				
				var dep = map.get(result.getInt("departmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(result);
					map.put(result.getInt("departmentid"), dep);
				}
				
				
				var seller = instatianteSeller(result, dep);
				
				sellers.add(seller);
				
			}
			return sellers;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
			
		}
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(query);
			
		}
	}
}