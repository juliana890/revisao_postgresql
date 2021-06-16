package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import DB.DB;
import entities.Order;
import entities.Product;
import entities.enums.OrderStatus;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
		
		Statement st = conn.createStatement();
		
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order " + 
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " + 
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

		//Criamos um map para não haver duplicidade dos pedidos
		Map<Long, Order> orderMap = new HashMap<>();
		
		//Criamos um map para produto para quando um produto for instanciado não haver repetição do mesmo em outros pedidos
		Map<Long, Product> productMap = new HashMap<>();
		
		while(rs.next()) {
			Long order_id = rs.getLong("order_id");
			Long product_id = rs.getLong("product_id");
			
			//Verificamos se o order_id existe dentro do orderMap
			if(orderMap.get(order_id) == null) {
				//Se for pedido diferente instanciamos o objeto
				Order order = instantiateOrder(rs);
				
				//Adicionamos o order_id e o order no orderMap
				orderMap.put(order_id, order);
			}
			
			//Verificamos se o product_id existe dentro do productMap
			if(productMap.get(product_id) == null) {
				//Se for produto diferente instanciamos o objeto
				Product product = instantiateProduct(rs);
				
				//Adicionamos o product_id e o product no productMap
				productMap.put(product_id, product);
			}
			
			//Associamos os pedidos com cada produto
			orderMap.get(order_id).getProducts().add(productMap.get(product_id));
		}
		
		//Imprimindo os pedidos
		//orderMap.keySet() busca todos os ids contidos nele
		for(Long orderId : orderMap.keySet()) {
			System.out.println(orderMap.get(orderId));
			
			//Imprimindo os produtos de cada pedido
			for(Product p : orderMap.get(orderId).getProducts()) {
				System.out.println(p);
			}
			
			System.out.println();
		}
		
		DB.closeConnection();
		DB.closeStatement(st);
		DB.closeResultSet(rs);
	}
	
	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product prod = new Product();
		prod.setId(rs.getLong("product_id"));
		prod.setName(rs.getString("name"));
		prod.setDescription(rs.getString("description"));
		prod.setImageUri(rs.getString("image_uri"));
		prod.setPrice(rs.getDouble("price"));
		
		return prod;
		
	}
	
	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		
		return order;
	}

}
