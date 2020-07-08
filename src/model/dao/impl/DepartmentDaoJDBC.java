package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.dao.DepartmentDao;
import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao { 
	
	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement query = null;
		try {
			query = conn.prepareStatement(
					"INSERT INTO department (name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS
					);
			
			query.setString(1, obj.getName());
			
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
	public void update(Department obj) {
		PreparedStatement query = null;
		try {
			query = conn.prepareStatement(
					"UPDATE department SET name=? WHERE id=?"
					);
			
			query.setString(1, obj.getName());
			query.setInt(2, obj.getId());
			
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
					"DELETE FROM department WHERE id=?"
					);
			query.setInt(1, id);
			
			query.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
			
		}
		finally {
			DB.closeStatement(query);
			
		}
		
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement query = null;
		ResultSet result = null;
		
		try {
			query = conn.prepareStatement(
					"SELECT * FROM department WHERE id = ?"
					);
			
			query.setInt(1, id);
			result = query.executeQuery();
			
			if (result.next()) {
				var dep = instantiateDepartment(result);
				
				return dep;
				
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
	
	private Department instantiateDepartment(ResultSet result) throws SQLException {
		var dep = new Department();
		dep.setId(result.getInt("id"));
		dep.setName(result.getString("name"));
		
		return dep;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement query = null;
		ResultSet result = null;
		
		try {
			query = conn.prepareStatement(
					"SELECT * FROM department"
					);
			
			result = query.executeQuery();
			
			List<Department> departments = new ArrayList<>();
			
			while (result.next()) {
				
				var dep = instantiateDepartment(result);
				
				
				departments.add(dep);
				
			}
			return departments;
			
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
