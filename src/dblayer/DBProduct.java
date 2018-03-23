package dblayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import dblayer.interfaces.IFDBProduct;
import modlayer.Product;

public class DBProduct implements IFDBProduct {

	private Connection con;

	public DBProduct() {
		con = DBConnection.getConnection();
	}

	@Override
	public ArrayList<Product> getProducts() {
		ArrayList<Product> products = new ArrayList<>();

		String query = "SELECT * FROM Product";
		try {
			
			Statement st = con.createStatement();
			st.setQueryTimeout(5);
			
			Product product;
			ResultSet results = st.executeQuery(query);
			while (results.next()) {
				product = buildProduct(results);
				products.add(product);
			}
			st.close();
		} catch (SQLException e) {
			System.out.println("Products were not found!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
		
		return products;
	}
	@Override
	public ArrayList<Product> searchProducts(String keyword) {
		ArrayList<Product> products = new ArrayList<>();

		String query =
				  "SELECT * FROM Product "
				+ "WHERE id LIKE '%?%' "
				+ "OR name LIKE '%?%' "
				+ "OR desc LIKE '%?%'";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setQueryTimeout(5);
			ps.setString(1, keyword);
			ps.setString(2, keyword);
			ps.setString(3, keyword);
			
			Product product;
			ResultSet results = ps.executeQuery(query);
			while (results.next()) {
				product = buildProduct(results);
				products.add(product);
			}
			ps.close();
		}
		catch (SQLException e) {
			System.out.println("Products were not found!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
		
		return products;
	}
	@Override
	public Product selectProduct(int productId) {
		Product product = null;
		
		String query = "SELECT * FROM Product WHERE id = ?";
		try {
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setQueryTimeout(5);
			ps.setInt(1, productId);
			
			ResultSet results = ps.executeQuery();
			if (results.next()) {
				product = buildProduct(results);
			}
		} catch (SQLException e) {
			System.out.println("Product was not found!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
		
		return product;
	}
	@Override
	public int insertProduct(Product product) {
		int id = -1;
		
		String query =
				  "INSERT INTO Product "
				+ "(name, purchase_price, sales_price, rent_price, origin_country, description, stock, min_stock) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			
			PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setQueryTimeout(5);
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPurchasePrice());
			ps.setDouble(3, product.getSalesPrice());
			ps.setDouble(4, product.getRentPrice());
			ps.setString(5, product.getCountryOfOrigin());
			ps.setString(6,  product.getDesc());
			ps.setInt(7,  product.getStock());
			ps.setInt(8, product.getMinStock());
			
			if (ps.executeUpdate() > 0) {
				ResultSet generatedKeys = ps.getGeneratedKeys();
	            if (generatedKeys.next()) {
	            	id = generatedKeys.getInt(1);
		            product.setId(id);
	            }
			}
			ps.close();
			
		}
		catch (SQLException e) {
			System.out.println("Product was not inserted!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
		
		return id;
	}
	@Override
	public boolean updateProduct(Product product) {
		boolean success = false;
		
		String query =
				    "UPDATE Product "
				  + "SET name = ? "
				  + ",purchase_price = ? "
				  + ",sales_price = ? "
				  + ",rent_price = ? "
				  + ",origin_country = ? "
				  + ",description = ? "
				  + ",stock = ? "
				  + ",min_stock = ? "
				  + "WHERE id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setQueryTimeout(5);
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPurchasePrice());
			ps.setDouble(3, product.getSalesPrice());
			ps.setDouble(4, product.getRentPrice());
			ps.setString(5, product.getCountryOfOrigin());
			ps.setString(6,  product.getDesc());
			ps.setInt(7,  product.getStock());
			ps.setInt(8, product.getMinStock());
			ps.setInt(9, product.getId());
			
			success = ps.executeUpdate() > 0;
			ps.close();
		}
		catch (SQLException e) {
			System.out.println("Product was not updated!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
		
		return success;
	}
	@Override
	public boolean deleteProduct(Product product) {
		boolean success = false;

		String query = "DELETE FROM Product WHERE id = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setQueryTimeout(5);
			ps.setInt(1, product.getId());
			
			success = ps.executeUpdate() > 0;
			ps.close();
		}
		catch (SQLException e) {
			System.out.println("Product stock was not decreased!");
			System.out.println(e.getMessage());
			System.out.println(query);
		}
			
		return success;
	}
	
	private Product buildProduct(ResultSet results) throws SQLException {
		Product product = null;
		
		try {
			
			product = new Product();
			product.setId(results.getInt("id"));
			product.setName(results.getString("name"));
			product.setPurchasePrice(results.getDouble("purchase_price"));
			product.setSalesPrice(results.getDouble("sales_price"));
			product.setRentPrice(results.getDouble("rent_price"));
			product.setCountryOfOrigin(results.getString("origin_country"));
			product.setDesc(results.getString("description"));
			product.setStock(results.getInt("stock"));
			product.setMinStock(results.getInt("min_stock"));
		}
		catch (SQLException e) {
			System.out.println("Product was not built!");
			System.out.println(e.getMessage());
			
			throw e;
		}
		
		return product;
	}
}
